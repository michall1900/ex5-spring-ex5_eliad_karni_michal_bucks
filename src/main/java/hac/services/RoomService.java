package hac.services;

import com.fasterxml.jackson.databind.ObjectMapper;
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
import org.springframework.beans.factory.annotation.Qualifier;
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

    @Autowired
    private RoomRepository roomRepo;

    @Autowired
    private PlayerRepository playersRepo;

    @Autowired
    private PlayerService playerService;

    @Autowired
    private BoardService boardService;
//    @Resource(name="getRoomLock")
//    ReentrantReadWriteLock roomLock;
//
//    @Resource(name="getPlayerLock")
//    ReentrantReadWriteLock playerLock;

//    synchronized private void lockAll(){
//        roomLock.writeLock().lock();
//        playerLock.writeLock().lock();
//    }
    @Resource(name="getRoomExecutors")
    private Map<Long, ExecutorService> roomExecutors;

    @Resource(name="roomExecutorsLock")
    private ReentrantReadWriteLock executorsLock;

    @Transactional
    public Room saveRoom(Room room) {
        //try {
            //lockAll();
            return roomRepo.save(room);
        //}
//        finally {
//            roomLock.writeLock().unlock();
//            playerLock.writeLock().unlock();
//        }
    }
    @Transactional
    public Room createNewRoom(Player player, int type){
        Room room = new Room();
        room.setStatus(Room.RoomEnum.WAITING_FOR_NEW_PLAYER);
        room.setOption(Board.Options.values()[type]);
        room.add(player);
        return saveRoom(room);
    }

    @Transactional
    public void addPlayerToRoom(long roomId, Player newPlayer) {
        // TODO check transaction lock
        // TODO - check whats happened if player change an attribute.
        //try{
            //lockAll();
            Room room = roomRepo.findById(roomId).orElseThrow(() -> new RuntimeException(ROOM_NOT_FOUND));
            if (playersRepo.findByUsername(newPlayer.getUsername()) != null) {
                throw new RuntimeException(PLAYER_IN_ROOM);
            }
            room.add(newPlayer);
            if(room.full()){
                room.setStatus(Room.RoomEnum.WAITING_FOR_BOARDS);
            }
            //roomRepo.save(room);
        //}
//        finally {
//            roomLock.writeLock().unlock();
//            playerLock.writeLock().unlock();
//        }
    }
    @Transactional
    public void changeRoomStatus(long roomId, Room.RoomEnum status){
    //    try{
      //      roomLock.writeLock().lock();
            Room room = roomRepo.findById(roomId).orElseThrow(() -> new RuntimeException(ROOM_NOT_FOUND));
            //System.out.println(room.getId());
            //System.out.println(room.getStatus());
            //System.out.println(status);
            room.setStatus(status);
            //roomRepo.save(room);
  //      }
//        finally {
//            roomLock.writeLock().unlock();
//        }
    }
    @Transactional(readOnly = true)
    public List<Room> getAllRooms(){
        //try{
            //roomLock.readLock().lock();
            return roomRepo.findAll();
        //}
//        finally {
//            roomLock.readLock().unlock();
//        }
    }
    @Transactional(readOnly = true)
    public List<String> getAllNotReadyPlayersNameInRoomByUsername(String username){
        List<String> waitingPlayersList = new ArrayList<>();
        List<Player> playersOnRoom = playerService.getRoomByUsername(username).getPlayers();
        playersOnRoom.forEach((player)->{
            if(player.getStatus() == Player.PlayerStatus.NOT_READY)
                waitingPlayersList.add(player.getUsername());
        });
        return waitingPlayersList;
    }

    @Transactional
    public void updateRoomStatusByUsername(String username){
        Room room = playerService.getRoomByUsername(username);
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
            playersOnRoom.forEach((player)->{
                player.setStatus(Player.PlayerStatus.ON_GAME);
            });
        }
    }


    @Transactional(readOnly = true)
    public List<String> getAllOpponentNamesByUsername(String username){
        List<String> namesList = new ArrayList<>();
        List<Player> playersOnRoom = playerService.getRoomByUsername(username).getPlayers();
        playersOnRoom.forEach((player)->{
            if (!Objects.equals(player.getUsername(), username))
                namesList.add(player.getUsername());
        });
        return namesList;
    }

    @Transactional(readOnly = true)
    public String getPlayerUsernameTurn(String username){
        Room room = playerService.getRoomByUsername(username);
        //System.out.println(room.getPlayers());
        //System.out.println(room.getPlayers().size());
        if (room.getPlayers().size()!= Room.SIZE)
            throw new RuntimeException(NOT_ENOUGH_PLAYERS);
        return room.getPlayers().get(room.getCurrentPlayerIndex()).getUsername();
    }
    @Transactional(readOnly = true)
    public void checkIfBothUsersAreInSameRoom(String currentUserName, String opponentUserName){
        Room room1 = playerService.getRoomByUsername(currentUserName);
        Room room2 = playerService.getRoomByUsername(opponentUserName);
        if(room1!=room2){
            throw new DbError();
        }
    }

    @Transactional(readOnly = true)
    public void validateTurn(String username){
        if (!getPlayerUsernameTurn(username).equals(username))
            throw new InvalidChoiceError(NOT_USER_TURN);
    }
    @Transactional(readOnly = true)
    public void validateOnGame(Room room){
        if (room.getStatus().equals(Room.RoomEnum.GAME_OVER))
            throw new GameOver(GAME_OVER);
    }
    @Transactional
    public void  setUpdates(String currentUserName, UserTurn userTurn){
        synchronized(this) {
            checkIfBothUsersAreInSameRoom(currentUserName, userTurn.getOpponentName());
            validateTurn(currentUserName);
            Room room = playerService.getRoomByUsername(currentUserName);
            validateOnGame(room);
            Board board = boardService.getUserBoardByUserName(userTurn.getOpponentName());
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
                setGameOver(room, currentUserName);
            }
        }
    }
    //TODO change the synchronized section to a room lock.
    @Transactional(readOnly = true)
    public List<UpdateObject> getUpdates(String username, int timestamp){
        synchronized (this){
            Room room = playerService.getRoomByUsername(username);
            validateOnGame(room);
            List<String> updatesStringArray= room.getUpdateObjects();
            List<UpdateObject> updateObjectList = new ArrayList<>();
            for (int i = timestamp; i< updatesStringArray.size(); i++){
                updateObjectList.add(UpdateObject.convertStringToObject(updatesStringArray.get(i)));
            }
            return updateObjectList;
        }
    }

    @Transactional
    public void setGameOver(Room room, String winnerName){
        room.getPlayers().forEach((player)->{
            if (winnerName.equals(player.getUsername()))
                player.setStatus(Player.PlayerStatus.WIN);
            else
                player.setStatus(Player.PlayerStatus.LOSE);
        });
        room.setStatus(Room.RoomEnum.GAME_OVER);
    }


    public ExecutorService getExecutorServiceForRoom(String username) {
        executorsLock.writeLock().lock();
        try {
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
            executorsLock.writeLock().unlock();
        }
    }

    public void shutdownExecutorServiceForRoom(String username) {
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

    //TODO when room closed, shut down its executor.
}

