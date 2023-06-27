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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;


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
            System.out.println(room.getId());
            System.out.println(room.getStatus());
            System.out.println(status);
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
        System.out.println(room.getPlayers());
        System.out.println(room.getPlayers().size());
        //TODO fix it.
        if (room.getPlayers().size()!= 2)
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
    public void setUpdates(String currentUserName, UserTurn userTurn){
        checkIfBothUsersAreInSameRoom(currentUserName, userTurn.getOpponentName());
        validateTurn(currentUserName);
        Room room = playerService.getRoomByUsername(currentUserName);
        Board board = boardService.getUserBoardByUserName(userTurn.getOpponentName());
        ArrayList<HashMap<String,String>> boardUpdates = board.getHitChanges(userTurn.getRow(), userTurn.getCol());
        UpdateObject updateObject = new UpdateObject();
        updateObject.setBoardChanges(boardUpdates);
        HashMap<String, String> detailsAboutUpdate = new HashMap<>();
        detailsAboutUpdate.put("attackerName", currentUserName);
        detailsAboutUpdate.put("opponentName", userTurn.getOpponentName());
        detailsAboutUpdate.put("row", Integer.toString(userTurn.getRow()));
        detailsAboutUpdate.put("col", Integer.toString(userTurn.getCol()));
        updateObject.setAttackDetails(detailsAboutUpdate);
        List<String> updatesArray = room.getUpdateObjects();
        updatesArray.add(updateObject.convertObjectToString());
        room.setUpdateObjects(updatesArray);
        Tile.TileStatus tileStatus = board.getBoardTiles().get(userTurn.getRow()*Board.SIZE + userTurn.getCol()).getStatus();
        if (tileStatus.equals(Tile.TileStatus.Miss)){
            room.setCurrentPlayerIndex((room.getCurrentPlayerIndex()+1)%room.getPlayers().size());
        }
        if (board.getExplodedSubmarine() == Board.SIZE){
            room.getPlayers().forEach((player)->{
                if (currentUserName.equals(player.getUsername()))
                    player.setStatus(Player.PlayerStatus.WIN);
                else
                    player.setStatus(Player.PlayerStatus.LOSE);
            });
            room.setStatus(Room.RoomEnum.GAME_OVER);
        }
        //TODO if board's size === hittedSubmarines change status to win(currentPlayer) / loss (other players)

    }

//    @Transactional(readOnly = true)
//    public String returnUpdates(int timestamp){
//
//    }
}

