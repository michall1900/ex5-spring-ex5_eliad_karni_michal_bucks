package hac.services;

import hac.beans.RoomLockHandler;
import hac.classes.customErrors.DbError;
import hac.classes.customErrors.GameOver;
import hac.classes.customErrors.InvalidChoiceError;
import hac.classes.forGame.UserTurn;
import hac.embeddables.UpdateObject;
import hac.filters.OnRoomFilter;
import hac.repo.board.Board;
import hac.repo.board.BoardRepository;
import hac.repo.player.Player;
import hac.repo.player.PlayerRepository;
import hac.repo.room.Room;
import hac.repo.room.RoomRepository;
import hac.repo.tile.Tile;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.context.request.async.DeferredResult;

import java.security.Principal;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
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

    final static String ALREADY_HAVE_BOARD = "You already have a board";

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

    @Autowired
    private BoardRepository boardRepository;

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
            setExecutor(room2.getId());
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
                validatePlayersOnGame(room);
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

    /**
     * Assumption - The function who called this function locked dbLock + room's lock.
     * @param room
     */
    private void checkIfGameFinished(Room room){
        if (room.getStatus().equals(Room.RoomEnum.GAME_OVER))
            throw new GameOver(GAME_OVER);
    }

    /**
     * Assumption - both room look and dblock are locked.
     * @param room
     */
    private void validatePlayersOnGame(Room room){
        if (!room.full())
            throw new RuntimeException(NOT_ENOUGH_PLAYERS);
    }
    @Transactional
    public void  setUpdates(String currentUserName, UserTurn userTurn){
        try {
            DBLock.readLock().lock();
            Room room = playerService.getRoomByUsername(currentUserName, false);
            roomsLock.getRoomLock(room.getId()).writeLock().lock();
            try {
                checkIfGameFinished(room);
                checkIfBothUsersAreInSameRoom(currentUserName, userTurn.getOpponentName());
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

    /**
     * The assumption is the dblock and the room's lock are locked.
     * @param username
     * @param timestamp
     * @return
     */
    private List<UpdateObject> getUpdates(String username, int timestamp){

        Room room = playerService.getRoomByUsername(username, false);
        checkIfGameFinished(room);
        List<String> updatesStringArray= room.getUpdateObjects();
        if (timestamp> updatesStringArray.size())
            throw new InvalidChoiceError(INVALID_TIME_STAMP);
        List<UpdateObject> updateObjectList = new ArrayList<>();
        for (int i = timestamp; i< updatesStringArray.size(); i++){
            updateObjectList.add(UpdateObject.convertStringToObject(updatesStringArray.get(i)));
        }
        return updateObjectList;

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

    /**
     * The assumption is dbLock + room db is locked for reading.
     * @param
     * @return
     */
    private Board.Options getBoardOptionByUsername(String username){
        Room room = playerService.getRoomByUsername(username, true);
        return room.getOption();
    }

    /**
     * The assumption is dbLock is locked for writing.
     * @param
     * @return
     */

    private void setExecutor(Long roomId){

        ExecutorService executorService = roomExecutors.get(roomId);
        int numPlayers = Room.SIZE;
        if (executorService != null) {
            throw new DbError();
        }
        executorService = Executors.newFixedThreadPool(numPlayers);
        roomExecutors.put(roomId, executorService);


    }

    /**
     *
     * Assumption - the method who locks this locked the db for reading.
     * @param username
     * @return
     */
    private ExecutorService getExecutorServiceForRoom(String username) {

        Long roomId = playerService.getRoomByUsername(username,false).getId();

        ExecutorService executorService = roomExecutors.get(roomId);
        if (executorService == null) {
            throw new DbError();
        }
        return executorService;

    }

    /**
     * Assumption - the method who locks this locked the db for writing.
     * @param roomId
     */
    private void shutdownExecutorServiceForRoom(Long roomId) {
        ExecutorService executorService = roomExecutors.get(roomId);
        executorService.shutdown();
        try {
            if (!executorService.awaitTermination(800, TimeUnit.MILLISECONDS)) {
                executorService.shutdownNow();
            }
        } catch (InterruptedException e) {
            executorService.shutdownNow();
        }
        finally {
            roomExecutors.remove(roomId);
        }


    }

    public DeferredResult<ResponseEntity<?>> handleStatusRoomPolling(Principal principal, DeferredResult<ResponseEntity<?>> output){
        try {
            DBLock.readLock().lock();
            Room room = playerService.getRoomByUsername(principal.getName(),false);
            //TODO maybe it is not great that the room itself is not blocked when we get this. need to be aware.
            Future<?> future = getExecutorServiceForRoom(principal.getName()).submit(() -> {
                try {
                    Room.RoomEnum status;
                    do {
                        roomsLock.getRoomLock(room.getId()).readLock().lock();
                        validatePlayersOnGame(room);
                        try{

                            status = playerService.getRoomStatusByUserName(principal.getName());
                            if (status != Room.RoomEnum.ON_GAME) {
                                roomsLock.getRoomLock(room.getId()).readLock().unlock();
                                Thread.sleep(200);
                            }
                        }
                        finally {
                            try {
                                roomsLock.getRoomLock(room.getId()).readLock().unlock();
                            }
                            catch(IllegalMonitorStateException e){
                                ;
                            }
                        }
                    } while (status != Room.RoomEnum.ON_GAME && !Thread.currentThread().isInterrupted());
                    if (!Thread.currentThread().isInterrupted())
                        output.setResult(ResponseEntity.ok("/game/on-game"));
                }
                catch (GameOver e){
                    output.setResult(ResponseEntity.ok("/game/finish-page"));
                }
                catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                catch (Exception e) {
                    output.setErrorResult(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage()));
                }
            });
            output.onTimeout(() -> {
                future.cancel(true);
                System.out.println("Timeout! Sent to" + principal.getName());
                output.setErrorResult(ResponseEntity.status(HttpStatus.GATEWAY_TIMEOUT).body("Service Unavailable"));
            });
        }
        catch (Exception e) {
            output.setErrorResult(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage()));
        }
        finally {
            DBLock.readLock().unlock();
        }

        return output;
    }

    public DeferredResult<ResponseEntity<?>> handleUpdatePolling(Principal principal, DeferredResult<ResponseEntity<?>> output, int timestamp){
        try {
            DBLock.readLock().lock();
            Room room = playerService.getRoomByUsername(principal.getName(), false);
            Future<?> future = getExecutorServiceForRoom(principal.getName()).submit(() -> {
                try {
                    List<UpdateObject> updates = new ArrayList<>();
                    do {
                        roomsLock.getRoomLock(room.getId()).readLock().lock();
                        validatePlayersOnGame(room);
                        try {
                            updates = getUpdates(principal.getName(), timestamp);
                            if (updates.isEmpty()) {
                                roomsLock.getRoomLock(room.getId()).readLock().unlock();
                                Thread.sleep(200);
                            }
                        } finally {
                            try {
                                roomsLock.getRoomLock(room.getId()).readLock().unlock();
                            } catch (IllegalMonitorStateException e) {
                                ;
                            }
                        }
                    }
                    while (updates.isEmpty() && !Thread.currentThread().isInterrupted());
                    if (!Thread.currentThread().isInterrupted())
                        output.setResult(ResponseEntity.ok(updates));
                }
                catch (InvalidChoiceError e){
                    output.setErrorResult(ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage()));
                }
                catch (GameOver e){
                    output.setResult(ResponseEntity.status(HttpStatus.OK).body("/game/finish-page"));
                }
                catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    //output.setErrorResult(ResponseEntity.status(HttpStatus.REQUEST_TIMEOUT).body("Service Unavailable"));
                }
                catch (Exception e) {
                    output.setErrorResult(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage()));
                }
            });
            output.onTimeout(() -> {
                future.cancel(true);
                System.out.println("Timeout! Sent to" + principal.getName());
                output.setErrorResult(ResponseEntity.status(HttpStatus.GATEWAY_TIMEOUT).body("Service Unavailable"));
            });
        }
        catch (Exception e) {
            output.setErrorResult(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage()));
        }
        finally {
            DBLock.readLock().unlock();
        }

        return output;
    }
    public void setOnGameModel(Model model, String username) {
        try {
            DBLock.readLock().lock();
            Long Id = playerService.getRoomByUsername(username, false).getId();
            roomsLock.getRoomLock(Id).readLock().lock();
            try {
                model.addAttribute("turn", getPlayerUsernameTurn(username, false,false));
                model.addAttribute("name", username);
                model.addAttribute("opponentBoards", boardService.getOpponentBoardsByUsername(username));
                model.addAttribute("myBoard", boardService.getUserTwoDimensionalArrayBoardByUsername(username));
            }
            finally {
                roomsLock.getRoomLock(Id).readLock().unlock();
            }
        }
        finally {
            DBLock.readLock().unlock();
        }
    }
    public void setGameInitModel(Model model, String username){
        try {
            DBLock.readLock().lock();
            Long Id = playerService.getRoomByUsername(username, false).getId();
            roomsLock.getRoomLock(Id).readLock().lock();
            try {
                model.addAttribute("names", getAllOpponentNamesByUsername(username, false, false));
                model.addAttribute("endValue", Board.SIZE-1);
                model.addAttribute("imgPath", Board.imgType.get(String.valueOf(Tile.TileStatus.Empty)));
                model.addAttribute("option", Board.options.get(getBoardOptionByUsername(username).ordinal()));
                model.addAttribute("url","/game/init");
            }
            finally {
                roomsLock.getRoomLock(Id).readLock().unlock();
            }
        }
        finally {
            DBLock.readLock().unlock();
        }
    }
    public String getValidationErrorForInitGame( String username){
        try {
            DBLock.readLock().lock();
            Long Id = playerService.getRoomByUsername(username, false).getId();
            roomsLock.getRoomLock(Id).readLock().lock();
            try {
                Room.RoomEnum roomStatus = playerService.getRoomStatusByUserName(username);
                Player.PlayerStatus playerStatus = playerService.getPlayerStatusByUsername(username);
                //If the player is not ready and the game is waiting for boards, it's ok to get into this path.
                if (roomStatus == Room.RoomEnum.WAITING_FOR_BOARDS && playerStatus == Player.PlayerStatus.NOT_READY)
                    return null;
                    //TODO change the location of the other fields and the errors about them. It's something more global.
                    //If player ready and the game is waiting for boards, that's mean that he tries to get to initial room again
                    // when he needs to be in waiting room
                else {
                    if (roomStatus == Room.RoomEnum.WAITING_FOR_BOARDS && playerStatus == Player.PlayerStatus.READY) {
                        return OnRoomFilter.BOARD_ALREADY_SENT;
                    }
                    //If the game is on and the player already in game, the user should get to the game path.
                    else if (roomStatus == Room.RoomEnum.ON_GAME && playerStatus == Player.PlayerStatus.ON_GAME) {
                        return OnRoomFilter.BOARD_ALREADY_SENT;

                    } else {
                        System.out.println("Invalid room status");
                        //TODO remove player from db + from room list. If we got there it's already exist
                        return OnRoomFilter.INVALID_STATUS;
                    }
                }
            }
            finally {
                roomsLock.getRoomLock(Id).readLock().unlock();
            }
        }
        finally {
            DBLock.readLock().unlock();
        }
    }

    @Transactional
    public void saveNewBoard(Board board, String username){
        try {
            DBLock.writeLock().lock();
            //TODO order the code.
            Player p = playerService.getPlayerByUsername(username,false);
            Room r = p.getRoom();
            if (p.getBoard()!=null)
                throw new RuntimeException(ALREADY_HAVE_BOARD);
            board.makeBoard(r.getOption());
            p.setBoard(board);
            p.setStatus(Player.PlayerStatus.READY);
            board.setPlayer(p);
            boardRepository.save(board);
            updateRoomStatusByUsername(username, false, false);
        }
        finally {
            DBLock.writeLock().unlock();
        }



    }

    @Transactional
    public void removePlayer(String username) throws RuntimeException{
        try {
            DBLock.writeLock().lock();
            Player player = playerService.getPlayerByUsername(username, false);
            Room room = player.getRoom();
            room.getPlayers().remove(player);
            if (room.getPlayers().isEmpty()) {
                roomsLock.removeLock(room.getId());
                shutdownExecutorServiceForRoom(room.getId());
                roomRepo.delete(room);
            }
        }finally {
            DBLock.writeLock().unlock();
        }
    }
    //TODO when room closed, shut down its executor.

    public void validatePlayerInRoomStatus(String username) throws Exception{
        try {
            DBLock.readLock().lock();
            Room room = playerService.getRoomByUsername(username, false);
            roomsLock.getRoomLock(room.getId()).readLock().lock();
            try {
                Room.RoomEnum roomStatus = playerService.getRoomStatusByUserName(username);
                if((roomStatus != Room.RoomEnum.WAITING_FOR_NEW_PLAYER && roomStatus != Room.RoomEnum.GAME_OVER)
                && !room.full())
                    throw new Exception("Player tried to enter the wrong room");
            }
            finally {
                roomsLock.getRoomLock(room.getId()).readLock().unlock();
            }
        }
        finally {
            DBLock.readLock().unlock();
        }
    }
}

