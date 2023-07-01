package hac.services;

import hac.beans.RoomLockHandler;
import hac.classes.customErrors.DbError;
import hac.classes.customErrors.GameOver;
import hac.classes.customErrors.InvalidChoiceError;
import hac.classes.forGame.UserTurn;
import hac.classes.forGame.UpdateObject;
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


/**
 * Service class for managing rooms in the game.
 */
@Service
public class RoomService {

    /**For room not found error*/
    static final String ROOM_NOT_FOUND ="Room not found";
    /**For players' already in room error*/
    static final String PLAYER_IN_ROOM = "Player is already in the room";
    /**For notify the game is over*/
    static final String GAME_OVER = "The game end";

    /**To note that there are not enough players in the room */
    static final String NOT_ENOUGH_PLAYERS = "Some of the players are disconnected";

    /**To note that it is not the current player's turn*/
    static final String NOT_USER_TURN = "It's not your turn";

    /**Note to player that he already have board*/
    final static String ALREADY_HAVE_BOARD = "You already have a board";

    /**To return the user that its timestamp invalid*/
    static final String INVALID_TIME_STAMP = "Your timestamp is invalid";

    /**Room's repository*/
    @Autowired
    private RoomRepository roomRepo;

    /**players repository*/
    @Autowired
    private PlayerRepository playersRepo;

    /**player's service*/
    @Autowired
    private PlayerService playerService;

    /**board's service*/
    @Autowired
    private BoardService boardService;

    /**room's executors map*/
    @Resource(name="getRoomExecutors")
    private Map<Long, ExecutorService> roomExecutors;

    /**The db lock*/
    @Resource(name = "getLockForAllDb")
    private ReentrantReadWriteLock DBLock;

    /**The rooms lock handler*/
    @Resource(name = "getRoomLock")
    private RoomLockHandler roomsLock;

    /**borad's repository*/
    @Autowired
    private BoardRepository boardRepository;

    /**
     * Creates a new room and adds a player to it. Locks the database write operation during execution to ensure thread safety.
     *
     * @param player The Player instance to be added to the new room.
     * @param type An integer representing the type of the room, corresponds to an index in the Board.Options enum.
     */
    @Transactional
    public void createNewRoom(Player player, int type){
        try {
            DBLock.writeLock().lock();
            Room room = new Room();
            room.setStatus(Room.RoomEnum.WAITING_FOR_NEW_PLAYER);
            room.setOption(Board.Options.values()[type]);
            room.add(player);
            Room room2 = roomRepo.save(room);
            roomsLock.setNewRoomLock(room2.getId());
            setExecutor(room2.getId());
        }finally {
            DBLock.writeLock().unlock();
        }
    }

    /**
     * Adds a new player to an existing room, identified by the room's ID. Locks the database write operation during
     * execution to ensure thread safety.
     * @param roomId The ID of the Room to which the player will be added.
     * @param newPlayer The Player instance to be added to the room.
     * @throws RuntimeException If the room does not exist, or if the player is already in a room.
     */
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

    /**
     * Retrieves all existing rooms. Locks the database read operation during execution to ensure thread safety.
     *
     * @return A List of all Room instances.
     */
    public List<Room> getAllRooms(){
        try {
            DBLock.readLock().lock();
            return roomRepo.findAll();
        }finally {
            DBLock.readLock().unlock();
        }
    }
//    @Transactional
//    public List<String> getAllNotReadyPlayersNameInRoomByUsername(String username){
//        List<String> waitingPlayersList = new ArrayList<>();
//        List<Player> playersOnRoom = playerService.getRoomByUsername(username).getPlayers();
//        playersOnRoom.forEach((player)->{
//            if(player.getStatus() == Player.PlayerStatus.NOT_READY)
//                waitingPlayersList.add(player.getUsername());
//        });
//        return waitingPlayersList;
//    }

    /**
     * Updates the status of the room associated with the specified user.
     * The method can lock a database and room based on passed parameters.
     * @param username The username of the user whose room's status should be updated.
     * @param toLockDB Boolean indicating if database lock is required.
     * @param toLockRoom Boolean indicating if room lock is required.
     */
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

    /**
     * Retrieves the usernames of all the opponents in the room of the specified user.
     * @param username The username of the user whose opponents' usernames are to be retrieved.
     * @param toLockDB Boolean indicating if database lock is required.
     * @param toLockRoom Boolean indicating if room lock is required.
     * @return A List of usernames of the opponents.
     */
    public List<String> getAllOpponentNamesByUsername(String username, boolean toLockDB, boolean toLockRoom){
        try {
            if(toLockDB)
                DBLock.readLock().lock();
            Room room = playerService.getRoomByUsername(username, false);
            if(toLockRoom)
                roomsLock.getRoomLock(room.getId()).readLock().lock();
            try {
                room = playerService.getRoomByUsername(username, false);
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

    /**
     * Retrieves the username of the player whose turn it is in the room of the specified user.
     * @param username The username of the user whose room's current player's username is to be retrieved.
     * @param toLockDB Boolean indicating if database lock is required.
     * @param toLockRoom Boolean indicating if room lock is required.
     * @return The username of the player whose turn it is.
     */
    public String getPlayerUsernameTurn(String username, boolean toLockDB, boolean toLockRoom){
        try {
            if(toLockDB)
                DBLock.readLock().lock();
            Room room = playerService.getRoomByUsername(username, false);
            if(toLockRoom)
                roomsLock.getRoomLock(room.getId()).readLock().lock();
            try {
                room = playerService.getRoomByUsername(username, false);
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
    /**
     * Checks if two users are in the same room. If not, throws a DbError.
     * @param currentUserName The username of the first user.
     * @param opponentUserName The username of the second user.
     * @throws DbError If the two users are not in the same room.
     */
    private void checkIfBothUsersAreInSameRoom(String currentUserName, String opponentUserName, Room currentRoom){
        Room room2 = playerService.getRoomByUsername(opponentUserName, false);
        roomsLock.getRoomLock(room2.getId()).readLock().lock();
        try{
            room2 = playerService.getRoomByUsername(opponentUserName, false);
            if(currentRoom!=room2){
                throw new DbError();
            }
        }
        finally {
            roomsLock.getRoomLock(room2.getId()).readLock().unlock();
        }

    }

    /**
     * Validates if it is the turn of the specified user. If not, throws an InvalidChoiceError.
     * @param username The username of the user to validate.
     * @throws InvalidChoiceError If it's not the user's turn.
     */
    private void validateTurn(String username){
        if (!getPlayerUsernameTurn(username, false, false).equals(username))
            throw new InvalidChoiceError(NOT_USER_TURN);
    }

    /**
     * Checks if the game in the specified room has finished. Throws a GameOver exception if the game is over.
     * This function assumes that the calling function has locked the DBLock and the room's lock.
     * @param room The room where the game to check is located.
     * @throws GameOver If the game is over.
     */
    private void checkIfGameFinished(Room room){
        if (room.getStatus().equals(Room.RoomEnum.GAME_OVER))
            throw new GameOver(GAME_OVER);
    }

    /**
     * Validates that all players in the specified room are in game. Throws a RuntimeException if not all players are in game.
     * This function assumes that both the DBLock and the room's lock are locked.
     * @param room The room where the game to validate is located.
     * @throws RuntimeException If not all players are in game.
     */
    private void validatePlayersOnGame(Room room){
        if (!room.full())
            throw new RuntimeException(NOT_ENOUGH_PLAYERS);
    }

    /**
     * Processes and applies the updates of the game based on a user's turn.
     * @param currentUserName The username of the current player.
     * @param userTurn The user's turn details.
     */
    @Transactional
    public void setUpdates(String currentUserName, UserTurn userTurn){
        try {
            DBLock.readLock().lock();
            Room room = playerService.getRoomByUsername(currentUserName, false);
            roomsLock.getRoomLock(room.getId()).writeLock().lock();
            try {
                room = playerService.getRoomByUsername(currentUserName, false);
                validateGameMode(room);
                checkIfBothUsersAreInSameRoom(currentUserName, userTurn.getOpponentName(), room);
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
     * Retrieves a list of update objects for the specified user starting from a specific timestamp.
     * The function assumes that the DBLock and the room's lock are locked.
     * @param username The username of the user whose updates are to be retrieved.
     * @param timestamp The timestamp from where to start retrieving the updates.
     * @return A list of UpdateObject instances.
     */
    private List<UpdateObject> getUpdates(String username, int timestamp){

        Room room = playerService.getRoomByUsername(username, false);
        validateGameMode(room);
        List<String> updatesStringArray= room.getUpdateObjects();
        if (timestamp > updatesStringArray.size())
            throw new InvalidChoiceError(INVALID_TIME_STAMP);
        List<UpdateObject> updateObjectList = new ArrayList<>();
        for (int i = timestamp; i< updatesStringArray.size(); i++){
            updateObjectList.add(UpdateObject.convertStringToObject(updatesStringArray.get(i)));
        }
        return updateObjectList;

    }

    /**
     * Sets the game over status for the room and updates player statuses.
     * The function can lock database and room based on passed parameters.
     * @param room The room where the game has ended.
     * @param winnerName The name of the winning player.
     * @param toLockDB Boolean indicating if database lock is required.
     * @param toLockRoom Boolean indicating if room lock is required.
     */
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
     * Retrieves the board option by username. The function assumes that DBLock and room DB are locked for reading.
     * @param username The username of the player whose board option is to be retrieved.
     * @return The board option of the user.
     */
    private Board.Options getBoardOptionByUsername(String username){
        Room room = playerService.getRoomByUsername(username, true);
        return room.getOption();
    }

    /**
     * Sets the executor service for the room with the given ID.
     * The function assumes that DBLock is locked for writing.
     * @param roomId The ID of the room for which the executor service is to be set.
     * @throws DbError If the key is already in the map.
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
     * Retrieves the executor service for the room where the user with the specified username is located.
     * The function assumes that the method that locks this has locked the DB for reading.
     * @param username The username of the user whose room's executor service is to be retrieved.
     * @return The executor service of the room.
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
     * Shuts down the executor service for the room with the given ID.
     * The function assumes that the method that locks this has locked the DB for writing.
     * @param roomId The ID of the room for which the executor service is to be shut down.
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
    /**
     * Handles the status room polling.
     * @param principal The authenticated user.
     * @param output The deferred result for the polling.
     * @return The deferred result with the updated status.
     */
    public DeferredResult<ResponseEntity<?>> handleStatusRoomPolling(Principal principal, DeferredResult<ResponseEntity<?>> output){
        try {
            DBLock.readLock().lock();

            Future<?> future = getExecutorServiceForRoom(principal.getName()).submit(() -> {
                try {
                    Room.RoomEnum status;
                    do {
                        Room room = playerService.getRoomByUsername(principal.getName(),false);
                        roomsLock.getRoomLock(room.getId()).readLock().lock();

                        try{
                            room = playerService.getRoomByUsername(principal.getName(),false);
                            validatePlayersOnGame(room);
                            status = playerService.getRoomStatusByUserName(principal.getName());
                            if (status != Room.RoomEnum.ON_GAME) {
                                if (status == Room.RoomEnum.GAME_OVER)
                                    throw new GameOver(GAME_OVER);
                                if (status!= Room.RoomEnum.WAITING_FOR_BOARDS ||
                                        playerService.getPlayerStatusByUsername(principal.getName())!= Player.PlayerStatus.READY){
                                    throw new DbError();
                                }
                                roomsLock.getRoomLock(room.getId()).readLock().unlock();
                                Thread.sleep(200);
                            }
                        }
                        finally {
                            try {
                                roomsLock.getRoomLock(room.getId()).readLock().unlock();
                            }
                            catch(IllegalMonitorStateException e){

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
    /**
     * Handles the update polling.
     * @param principal The authenticated user.
     * @param output The deferred result for the polling.
     * @param timestamp The timestamp of the last update.
     * @return The deferred result with the updates.
     */
    public DeferredResult<ResponseEntity<?>> handleUpdatePolling(Principal principal, DeferredResult<ResponseEntity<?>> output, int timestamp){
        try {
            DBLock.readLock().lock();
            Room room = playerService.getRoomByUsername(principal.getName(), false);
            Future<?> future = getExecutorServiceForRoom(principal.getName()).submit(() -> {
                try {
                    List<UpdateObject> updates = new ArrayList<>();
                    do {
                        roomsLock.getRoomLock(room.getId()).readLock().lock();
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
    /**
     * Sets up the game model for a specific user.
     * @param model The Model in which the game details will be stored.
     * @param username The name of the user for whom the model will be set.
     */
    public void setOnGameModel(Model model, String username) {
        try {
            DBLock.readLock().lock();
            Room room = playerService.getRoomByUsername(username, false);
            roomsLock.getRoomLock(room.getId()).readLock().lock();
            try {
                room = playerService.getRoomByUsername(username, false);
                validateGameMode(room);
                model.addAttribute("turn", getPlayerUsernameTurn(username, false,false));
                model.addAttribute("name", username);
                model.addAttribute("opponentBoards", boardService.getOpponentBoardsByUsername(username));
                model.addAttribute("myBoard", boardService.getUserTwoDimensionalArrayBoardByUsername(username));
            }
            finally {
                roomsLock.getRoomLock(room.getId()).readLock().unlock();
            }
        }
        finally {
            DBLock.readLock().unlock();
        }
    }

    /**
     * Validates the game mode. This function assumes that DBLock and the room's lock are both locked.
     * @param room The room for which the game mode will be validated.
     * @throws DbError if the room is not on_game status.
     */
    private void validateGameMode(Room room){
        checkIfGameFinished(room);
        validatePlayersOnGame(room);
        if (room.getStatus()!= Room.RoomEnum.ON_GAME){
            throw new DbError();
        }
    }

    /**
     * Sets up the game initialization model for a specific user.
     * @param model The Model in which the game initialization details will be stored.
     * @param username The name of the user for whom the model will be set.
     */
    public void setGameInitModel(Model model, String username){
        try {
            DBLock.readLock().lock();
            Room room = playerService.getRoomByUsername(username, false);
            roomsLock.getRoomLock(room.getId()).readLock().lock();
            try {
                room = playerService.getRoomByUsername(username, false);
                validatePlayersOnGame(room);
                checkIfGameFinished(room);
                model.addAttribute("names", getAllOpponentNamesByUsername(username, false, false));
                model.addAttribute("endValue", Board.SIZE - 1);
                model.addAttribute("imgPath", Board.imgType.get(String.valueOf(Tile.TileStatus.Empty)));
                model.addAttribute("option", Board.options.get(getBoardOptionByUsername(username).ordinal()));
                model.addAttribute("url", "/game/init");

            }
            finally {
                roomsLock.getRoomLock(room.getId()).readLock().unlock();
            }
        }
        finally {
            DBLock.readLock().unlock();
        }
    }
    /**
     * Validates the state in /game/init for a specific user.
     * @param username The name of the user to validate.
     * @param lockDb Flag to determine whether the database should be locked for this operation.
     * @param lockRoom Flag to determine whether the room should be locked for this operation.
     * @return A String message indicating any validation errors. Null if no errors are found.
     */
    public String getValidationErrorForInitGame( String username, Boolean lockDb, Boolean lockRoom){
        try {
            if (lockDb)
                DBLock.readLock().lock();
            Room room = playerService.getRoomByUsername(username, false);
            if(lockRoom) {
                roomsLock.getRoomLock(room.getId()).readLock().lock();
            }
            try {
                if (lockRoom)
                    room = playerService.getRoomByUsername(username, false);
                validatePlayersOnGame(room);
                Room.RoomEnum roomStatus = playerService.getRoomStatusByUserName(username);
                Player.PlayerStatus playerStatus = playerService.getPlayerStatusByUsername(username);
                //If the player is not ready and the game is waiting for boards, it's ok to get into this path.
                if (roomStatus == Room.RoomEnum.WAITING_FOR_BOARDS && playerStatus == Player.PlayerStatus.NOT_READY)
                    return null;

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
                        return OnRoomFilter.INVALID_STATUS;
                    }
                }
            }
            finally {
                if (lockRoom)
                    roomsLock.getRoomLock(room.getId()).readLock().unlock();
            }
        }
        finally {
            if(lockDb)
                DBLock.readLock().unlock();
        }
    }

    /**
     * Saves a new board for a specific user.
     * This method is annotated with @Transactional, meaning it should be run in a transaction context.
     * @param board The board to be saved.
     * @param username The name of the user for whom the board will be saved.
     */
    @Transactional
    public void saveNewBoard(Board board, String username){
        try {
            DBLock.writeLock().lock();
            Player player = playerService.getPlayerByUsername(username,false);
            Room room = player.getRoom();
            validatePlayersOnGame(room);
            if (player.getBoard()!=null)
                throw new RuntimeException(ALREADY_HAVE_BOARD);
            board.makeBoard(room.getOption());
            player.setBoard(board);
            player.setStatus(Player.PlayerStatus.READY);
            board.setPlayer(player);
            boardRepository.save(board);
            updateRoomStatusByUsername(username, false, false);
        }
        finally {
            DBLock.writeLock().unlock();
        }
    }

    /**
     * Removes a player from the game.
     * This method is annotated with @Transactional, meaning it should be run in a transaction context.
     * @param username The name of the player to be removed.
     */
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

    /**
     * Validates the status of a player in a room.
     * @param username The name of the player to be validated.
     * @throws Exception If the player tries to enter an incorrect room.
     */
    public void validatePlayerInRoomStatus(String username) throws Exception{
        try {
            DBLock.readLock().lock();
            Room room = playerService.getRoomByUsername(username, false);
            roomsLock.getRoomLock(room.getId()).readLock().lock();
            try {
                room = playerService.getRoomByUsername(username, false);
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

