## Authors
* Name: Eliad Karni  Email: eliadka@edu.hac.ac.il
* Name: Michal Bucks Email: michalbu@edu.hac.ac.il

## Note
We used one free extra day: Eliad has two. In solange permission, we can use one day and not lose points about that. 

## Description

The project is a battleship board game implementation.

### General information
Taken from https://en.wikipedia.org/wiki/Battleship_(game) (also in how to play)

Battleship (also known as Battleships or Sea Battle) is a strategy type guessing game for two players. It is played on ruled grids (paper or board) on which each player's fleet of warships are marked. The locations of the fleets are concealed from the other player. Players alternate turns calling "shots" at the other player's ships, and the objective of the game is to destroy the opposing player's fleet.  Battleship is known worldwide as a pencil and paper game which dates from World War I. It was published by various companies as a pad-and-pencil game in the 1930s and was released as a plastic board game by Milton Bradley in 1967. The game has spawned electronic versions, video games, smart device apps and a film.

The game is played on four grids, two for each player. The grids are typically square – usually 10×10 – and the individual squares in the grid are identified by letter and number. On one grid the player arranges ships and records the shots by the opponent. On the other grid, the player records their own shots. Before play begins, each player secretly arranges their ships on their primary grid. Each ship occupies a number of consecutive squares on the grid, arranged either horizontally or vertically. The number of squares for each ship is determined by the type of ship. The ships cannot overlap (i.e., only one ship can occupy any given square in the grid). The types and numbers of ships allowed are the same for each player. These may vary depending on the rules. The ships should be hidden from players sight and it's not allowed to see each other's pieces. The game is a discovery game which players need to discover their opponents ship positions:

The 1990 Milton Bradley version of the rules specify the following ships which contains:\
1 ship of the size of 2 tiles\
2 ship of the size of 3 tiles\
1 ship of the size of 4 tiles\
1 ship of the size of 5 tiles

An alternative version of the rules specify the following ships:\
4 ship of the size of 1 tiles\
3 ship of the size of 2 tiles\
2 ship of the size of 3 tiles\
1 ship of the size of 4 tiles

After the ships have been positioned, the game proceeds in a series of rounds. In each round, each player takes a turn to announce a target square in the opponent's grid which is to be shot at. The opponent announces whether or not the square is occupied by a ship. If it is a "hit", the player who is hit marks this on their own or "ocean" grid (with a red peg in the pegboard version). The attacking player marks the hit or miss on their own "tracking" or "target" grid with a pencil marking in the paper version of the game, or the appropriate color peg in the pegboard version (red for "hit", white for "miss"), in order to build up a picture of the opponent's fleet. When all of the squares of a ship have been hit, the ship's owner announces the sinking of the Carrier, Submarine, Cruiser/Destroyer/Patrol Boat, or the titular Battleship. If all of a player's ships have been sunk, the game is over and their opponent wins.
#### How to navigate inside our website:
Enter the route "/" (usually port 8080 of localhost).\
Read the How to play.\
Register to the server (Note that the Username and the password length must be between 6 and 30 character long. However, they could not include space or blank).\
Login to the server.\
Enter the lobby.\
Create a room or enter one.\
If creating a room, choose the game type then create the room.\
wait for all the players to join the room.\
Arrange your ships and submit your board.\
Wait for the other players to submit their board.\
Each round, wait for your turn and try to hit the opponent's ships.\
When one of the players won, you'll be moved into an informative page that indicates the winner.\
Back to lobby.\
If you want you can have another game!\
#### Note: While playing, in most cases, when you click on another tab in the website/ navigate to another path, the room will be closed.
## Functionality

### Some things we implemented:
<ul>
<li>Polling: We are using a long polling on the game mode—to get updates from opponent's moves and for waiting to the opponent submit its board. We're also doing kind of long polling in the lobby + waiting room (wait to another player to connect).
</li>
<li>All the Users saves inside a database using Spring Security</li>
<li>All of the rooms data saves inside a databases. There are multiple relationships:
<ul><li>Room and Player have bidirectional relationship. Room holds one-to-many relation with Player while Player holds many-to-one relation with Room</li>
<li>Player have bidirectional relationship with Board, one-to-one relation.</li>
<li>Board have unidirectional with Submarine - one-to-many</li>
<li>Board have unidirectional with Tile - one-to-many</li>
<li>Tile have unidirectional with Submarine - many-to-one</li>
</ul>
</li>
<li>The client handle with the first board's display and send the result to the server.
</li>
</ul>

### Important things about returned statuses in /game pages: 
<ul>
<li>
Status 200 could be return also with a body tells doing redirect. It happened in cases the game is over. If the response's body starts with "/", move to this location in rest requests.
</li>
<li>
Status 504 returned in server's long polling timeout. Just ignore it and keep send requests.
</li>
<li>
Status 400 tells the user that there was an error, but he can try to send the request again and fix the error at his side.
</li>
<li>Any other status tells there is a critical error. In most cases, it tells that the room closed. The user should redirect to "/lobby/error-message"</li>
</ul>

### Rest controller:
We implement a rest controller to get and send updates to the board and also handle with long polling.

<ul>
<li>
    /game/update - A post-request endpoint. The client needs to send an object looking like that: {row:___, col:___ , opponentName:___} when row is the row's index in the board, col is the column index in the board, and the opponentName is the attacked player's username.
</li>
<li>/game/update/{timestamp} - A get-request endpoint. This is a long polling route. The client needs to send its current timestamp, which is the index of the last update he gets in the updates' list (room holds it). At first, it should be initialized to 0.
On timeout, the server sends status 504.\
The returned object looking like that:\ 
[{attackDetails:{attackerName:___, opponentName:___, row:___, col:___}, boardChanges:[{row:___, col:___, status:"Hit"/"Miss"}, {..}, ...]}, {...},...]
Rows and columns need to be 0 &lt;= integer &lt; Board.Size.
attackerName is the username of the attacker, opponentName is the attacked player's username. The reason that the returned board changes is a list is for cases that the change happened in more than one tile(submarine sink). 
</li>
<li>
    /game/wait-to-start - A get-request endpoint. This is a long polling route. The request needs to resend until an error occurred or status ok returned.  
</li>
</ul>

### How to send the board (for client side)?
You need to send an object looking like that:
{submarines: [{firstRow: ___, firstCol: ___, lastRow: ___, lastCol: ___, size: ___}, {...},...]}

## Installation
All the project is stored in memory and therefore no spacial installation is needed.

## Useful information
For easy usage, there are built in users:\
username: 0, password: 0, role: Admin\
username: 1, password: 1, role: User\
username: 2, password: 2, role: User

also, to see the database state you can preform a get rest to /game/test/print 