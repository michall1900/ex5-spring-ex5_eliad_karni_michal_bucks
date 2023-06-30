(function (){
    const ROOM_PLAYER_TABLE_BODY_ID = "room-player-body-table-body";
    let roomPlayersTableBodyElement;
    let updateRate = 1000 * 3;
    let badResponseCounter = 0;
    document.addEventListener("DOMContentLoaded", ()=>{
        roomPlayersTableBodyElement = document.getElementById(ROOM_PLAYER_TABLE_BODY_ID);
        subscribe();
    });

    function checkAnswer(roomJson){
        if( ! roomJson.hasOwnProperty("players")){
            window.location.href = "/lobby";
        }
    }
    function checkGameStarted(roomJson){
        if(roomJson.hasOwnProperty("start_game")) {
            window.location.href = "/game/init";
        }
    }


    function setRoomTable(roomsJson){
        roomPlayersTableBodyElement.innerHTML = "";
        JSON.parse(roomsJson.players).forEach((player) => {
            roomPlayersTableBodyElement.innerHTML += `
                <tr class="table-secondary">
                    <td>${player}</td>
                </tr>`;
        })
    }

    async function subscribe() {
        try {
            let response = await fetch("/lobby/getRoom");
            if (response.status === 502) {
                // Status 502 is a connection timeout error,
                // may happen when the connection was pending for too long,
                // and the remote server or a proxy closed it
                // let's reconnect
                await new Promise(resolve => setTimeout(resolve, updateRate));
                await subscribe();
            } else if (response.status !== 200) {
                await new Promise(resolve => setTimeout(resolve, updateRate));
                await subscribe();
            } else {
                // Get and show the message
                let message = await response.json();
                checkAnswer(message);
                setRoomTable(message);
                checkGameStarted(message);
                await new Promise(resolve => setTimeout(resolve, updateRate));
                await subscribe()
            }
        }catch (e){
            badResponseCounter++;
            if(badResponseCounter === 3){
                window.location.href = "/lobby";
            }
            await new Promise(resolve => setTimeout(resolve, updateRate));
            await subscribe()
        }
    }
})();