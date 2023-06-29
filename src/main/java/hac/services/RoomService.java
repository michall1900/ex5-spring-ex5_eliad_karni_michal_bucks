package hac.services;

import hac.beans.RoomLockHandler;
import hac.classes.customErrors.DbError;
import hac.classes.customErrors.GameOver;
import hac.classes.customErrors.InvalidChoiceError;
import hac.classes.forGame.UserTurn;
import hac.embeddables.UpdateObject;
import hac.repo.board.Board;
import hac.repo.player.Player;
import hac.repo.player.PlayerRepository;
import hac.repo.room.Room;
import hac.repo.room.RoomRepository;
import hac.repo.tile.Tile;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantReadWriteLock;


@Service
public class RoomService {

    static final String ROOM_NOT_FOUND ="Room not found";
    static final String PLAYER_IN_ROOM = "Player is already in the room";
    static final String GAME_OVER = "The game end";
    static final String NOT_ENOUGH_PLAYERS = "Some of the players are disconnecter";
    static final String NOT_USER_TURN = "It's not your turn";

    static final String INVALID_TIME_STAMP = "Your timestamp is invalid";

    @Autowired
    private RoomRepository roomRepo;

    @Autowired
    private PlayerRepository playersRepo;

    @Autowired
    private PlayerService playerService;

    @Autowired
    private BoardService boardService;

    @Resource(name="getRoomExecutors")
    private Map<Long, ExecutorService> roomExecutors;

    @Resource(name="roomExecutorsLock")
    private ReentrantReadWriteLock executorsLock;

    @Resource(name = "getLockForAllDb")
    private ReentrantReadWriteLock DBLock;

    @Resource(name = "getRoomLock")
    private RoomLockHandler roomsLock;

    @Transactional
    public Room createNewRoom(Player player, int type){
        try {
            DBLock.writeLock().lock();
            Room room = new Room();
            room.setStatus(Room.RoomEnum.WAITING_FOR_NEW_PLAYER);
            room.setOption(Board.Options.values()[type]);
            room.add(player);
            Room room2 = roomRepo.save(room);
            roomsLock.setNewRoomLock(room2.getId());
            return room2;
        }finally {
            DBLock.writeLock().unlock();
        }
    }

    @Transactional
    public void addPlayerToRoom(long roomId, Player newPlayer) {
        try {
            DBLock.writeLock().lock();
            Room room = roomRepo.findById(roomId).orElseThrow(() -> new RuntimeException(ROOM_NOT_FOUND));
            if (playersRepo.findByUsername(newPlayer.getUsername()) != null) {
                throw new RuntimeException(PLAYER_IN_ROOM);
            }
            room.add(newPlayer);
            if (room.full()) {
                room.setStatus(Room.RoomEnum.WAITING_FOR_BOARDS);
            }
        }finally {
                DBLock.writeLock().unlock();
        }
    }

//    @Transactional
//    public void changeRoomStatus(long roomId, Room.RoomEnum status){
//        try {
//            DBLock.readLock().lock();
//            try {
//                roomsLock.getRoomLock(roomId).writeLock().lock();
//                Room room = roomRepo.findById(roomId).orElseThrow(() -> new RuntimeException(ROOM_NOT_FOUND));
//                room.setStatus(status);
//
//            }finally {
//                roomsLock.getRoomLock(roomId).writeLock().unlock();
//            }
//        }finally {
//            DBLock.readLock().unlock();
//        }
//    }
    public List<Room> getAllRooms(){
        try {
            DBLock.readLock().lock();
            return roomRepo.findAll();
        }finally {
            DBLock.readLock().unlock();
        }
    }
//    @Transactional(readOnly = true)
//    public List<String> getAllNotReadyPlayersNameInRoomByUsername(String username){
//        List<String> waitingPlayersList = new ArrayList<>();
//        List<Player> playersOnRoom = playerService.getRoomByUsername(username).getPlayers();
//        playersOnRoom.forEach((player)->{
//            if(player.getStatus() == Player.PlayerStatus.NOT_READY)
//                waitingPlayersList.add(player.getUsername());
//        });
//        return waitingPlayersList;
//    }

    @Transactional
    public void updateRoomStatusByUsername(String username, boolean toLockDB, boolean toLockRoom){
        try {
            if(toLockDB)
                DBLock.readLock().lock();
            Room room = playerService.getRoomByUsername(username, false);
            if(toLockRoom)
                roomsLock.getRoomLock(room.getId()).writeLock().lock();
            try {
                List<Player> playersOnRoom = room.getPlayers();
                AtomicInteger counterOfNotReady = new AtomicInteger();
                playersOnRoom.forEach((player)->{
                    if(player.getStatus() == Player.PlayerStatus.NOT_READY)
                        counterOfNotReady.addAndGet(1);
                });
                if (counterOfNotReady.get()==0){
                    room.setStatus(Room.RoomEnum.ON_GAME);
                    Random random = new Random();
                    room.setCurrentPlayerIndex(random.nextInt(playersOnRoom.size()));
                    playersOnRoom.forEach((player)-> player.setStatus(Player.PlayerStatus.ON_GAME));
                }
            }finally {
                if(toLockRoom)
                    roomsLock.getRoomLock(room.getId()).writeLock().unlock();
            }
        }finally {
            if (toLockDB)
                DBLock.readLock().unlock();
        }
    }

    public List<String> getAllOpponentNamesByUsername(String username, boolean toLockDB, boolean toLockRoom){
        try {
            if(toLockDB)
                DBLock.readLock().lock();
            Room room = playerService.getRoomByUsername(username, false);
            if(toLockRoom)
                roomsLock.getRoomLock(room.getId()).readLock().lock();
            try {
                List<Player> playersOnRoom = room.getPlayers();
                List<String> namesList = new ArrayList<>();
                playersOnRoom.forEach((player)->{
                    if (!Objects.equals(player.getUsername(), username))
                        namesList.add(player.getUsername());
                });
                return namesList;
            }finally {
                if(toLockRoom)
                    roomsLock.getRoomLock(room.getId()).readLock().unlock();
            }
        }finally {
            if (toLockDB)
                DBLock.readLock().unlock();
        }
    }


    public String getPlayerUsernameTurn(String username, boolean toLockDB, boolean toLockRoom){
        try {
            if(toLockDB)
                DBLock.readLock().lock();
            Room room = playerService.getRoomByUsername(username, false);
            if(toLockRoom)
                roomsLock.getRoomLock(room.getId()).readLock().lock();
            try {
                if (room.getPlayers().size()!= Room.SIZE)
                    throw new RuntimeException(NOT_ENOUGH_PLAYERS);
                return room.getPlayers().get(room.getCurrentPlayerIndex()).getUsername();
            }finally {
                if(toLockRoom)
                    roomsLock.getRoomLock(room.getId()).readLock().unlock();
            }
        }finally {
            if (toLockDB)
                DBLock.readLock().unlock();
        }
    }

    private void checkIfBothUsersAreInSameRoom(String currentUserName, String opponentUserName){
        Room room1 = playerService.getRoomByUsername(currentUserName, false);
        Room room2 = playerService.getRoomByUsername(opponentUserName, false);
        if(room1!=room2){
            throw new DbError();
        }
    }


    private void validateTurn(String username){
        if (!getPlayerUsernameTurn(username, false, false).equals(username))
            throw new InvalidChoiceError(NOT_USER_TURN);
    }

    private void validateOnGame(Room room){
        if (room.getStatus().equals(Room.RoomEnum.GAME_OVER))
            throw new GameOver(GAME_OVER);
    }
    @Transactional
    public void  setUpdates(String currentUserName, UserTurn userTurn){
        try {
            DBLock.readLock().lock();
            Room room = playerService.getRoomByUsername(currentUserName, false);
            roomsLock.getRoomLock(room.getId()).writeLock().lock();
            try {
                checkIfBothUsersAreInSameRoom(currentUserName, userTurn.getOpponentName());
                validateOnGame(room);
                validateTurn(currentUserName);
                Board board = playerService.getPlayerByUsername(userTurn.getOpponentName(),false).getBoard();
                ArrayList<HashMap<String, String>> boardUpdates = board.getHitChanges(userTurn.getRow(), userTurn.getCol());
                UpdateObject updateObject = new UpdateObject();
                updateObject.setBoardChanges(boardUpdates);
                HashMap<String, String> detailsAboutUpdate = new HashMap<>();
                detailsAboutUpdate.put("attackerName", currentUserName);
                detailsAboutUpdate.put("opponentName", userTurn.getOpponentName());
                detailsAboutUpdate.put("row", Integer.toString(userTurn.getRow()));
                detailsAboutUpdate.put("col", Integer.toString(userTurn.getCol()));
                Tile.TileStatus tileStatus = board.getBoardTiles().get(userTurn.getRow() * Board.SIZE + userTurn.getCol()).getStatus();
                int newIndexTurn = room.getCurrentPlayerIndex();
                if (tileStatus.equals(Tile.TileStatus.Miss)) {
                    newIndexTurn = (newIndexTurn + 1) % room.getPlayers().size();
                    room.setCurrentPlayerIndex(newIndexTurn);
                }
                String nextUserTurnName = room.getPlayers().get(newIndexTurn).getUsername();
                detailsAboutUpdate.put("nextTurn", nextUserTurnName);
                updateObject.setAttackDetails(detailsAboutUpdate);
                List<String> updatesArray = room.getUpdateObjects();
                updatesArray.add(updateObject.convertObjectToString());
                room.setUpdateObjects(updatesArray);
                if (board.getExplodedSubmarine() == board.getSubmarines().size()) {
                    setGameOver(room, currentUserName, false, false);
                }
            }finally {
                roomsLock.getRoomLock(room.getId()).writeLock().unlock();
            }
        }finally {
            DBLock.readLock().unlock();
        }
    }

    public List<UpdateObject> getUpdates(String username, int timestamp){
        try {
            DBLock.readLock().lock();
            Room room = playerService.getRoomByUsername(username, false);
            roomsLock.getRoomLock(room.getId()).readLock().lock();
            try {
                validateOnGame(room);
                List<String> updatesStringArray= room.getUpdateObjects();
                if (timestamp> updatesStringArray.size())
                    throw new InvalidChoiceError(INVALID_TIME_STAMP);
                List<UpdateObject> updateObjectList = new ArrayList<>();
                for (int i = timestamp; i< updatesStringArray.size(); i++){
                    updateObjectList.add(UpdateObject.convertStringToObject(updatesStringArray.get(i)));
                }
                return updateObjectList;
            }finally {
                roomsLock.getRoomLock(room.getId()).readLock().unlock();
            }
        }finally {
            DBLock.readLock().unlock();
        }
    }

    @Transactional
    public void setGameOver(Room room, String winnerName, boolean toLockDB, boolean toLockRoom){
        try {
            if (toLockDB)
                DBLock.readLock().lock();
            if (toLockRoom)
                roomsLock.getRoomLock(room.getId()).writeLock().lock();
            try {
                room.getPlayers().forEach((player) -> {
                    if (winnerName.equals(player.getUsername()))
                        player.setStatus(Player.PlayerStatus.WIN);
                    else
                        player.setStatus(Player.PlayerStatus.LOSE);
                });
                room.setStatus(Room.RoomEnum.GAME_OVER);
            }finally {
                if (toLockRoom)
                    roomsLock.getRoomLock(room.getId()).writeLock().unlock();
            }
        }finally {
            if (toLockDB)
                DBLock.readLock().unlock();
        }

    }


    public ExecutorService getExecutorServiceForRoom(String username) {
        try {
            executorsLock.writeLock().lock();
            DBLock.readLock().lock();
            Long roomId = playerService.getRoomByUsername(username).getId();
            int numPlayers = Room.SIZE;
            ExecutorService executorService = roomExecutors.get(roomId);
            if (executorService == null) {
                executorService = Executors.newFixedThreadPool(numPlayers);
                roomExecutors.put(roomId, executorService);
            }
            return executorService;
        }
        finally {
            DBLock.readLock().unlock();
            executorsLock.writeLock().unlock();
        }
    }

    private void shutdownExecutorServiceForRoom(String username) {
        executorsLock.writeLock().lock();
        try{
            Long roomId = playerService.getRoomByUsername(username).getId();
            ExecutorService executorService = roomExecutors.remove(roomId);
            executorService.shutdown();
            try {
                if (!executorService.awaitTermination(800, TimeUnit.MILLISECONDS)) {
                    executorService.shutdownNow();
                }
            } catch (InterruptedException e) {
                executorService.shutdownNow();
            }
        }
        finally {
            executorsLock.writeLock().unlock();
        }

    }

    private Board.Options getBoardOptionByUsername(String username){
        Room room = playerService.getRoomByUsername(username);
        return room.getOption();
    }

    //TODO when room closed, shut down its executor.
}

