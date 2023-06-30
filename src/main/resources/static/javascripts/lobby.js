(function (){
    const ROOMS_TABLE_BODY_ID = "rooms-table-body";
    let roomsTableBodyElement;
    let POLLING_RATE = 1;

    document.addEventListener("DOMContentLoaded", ()=>{
        roomsTableBodyElement = document.getElementById(ROOMS_TABLE_BODY_ID);

        subscribe();
    });

    function setRoomsTable(roomsJson){
        //TODO validate answer.
        roomsTableBodyElement.innerHTML = "";
        roomsJson.forEach((room) => {
            let owner = JSON.parse(room.players)[0]
            roomsTableBodyElement.innerHTML += `
                <tr class="table-secondary">
                    <td>${room.type}</td>
                    <td>${owner}</td>
                    <td>
                        <a href="/lobby/enter-room/${room.id}" class="btn btn-outline-danger" role="button">Enter</a>
                    </td>
                </tr>`;
        })
    }

    async function subscribe() {
        try {
            let response = await fetch("/lobby/getRooms");
            if (response.status === 502) {
                // Status 502 is a connection timeout error,
                // may happen when the connection was pending for too long,
                // and the remote server or a proxy closed it
                // let's reconnect
                await new Promise(resolve => setTimeout(resolve, POLLING_RATE * 1000));
                await subscribe();
            } else if (response.status !== 200) {
                // Reconnect in one second
                await new Promise(resolve => setTimeout(resolve, POLLING_RATE * 1000));
                await subscribe();
            } else {
                // Get and show the message
                let message = await response.json();
                // Call subscribe() again to get the next message
                setRoomsTable(message);
                await new Promise(resolve => setTimeout(resolve, POLLING_RATE * 1000));
                await subscribe()
            }
        }catch (e){
            await new Promise(resolve => setTimeout(resolve, POLLING_RATE * 1000));
            await subscribe()
        }
    }
})();