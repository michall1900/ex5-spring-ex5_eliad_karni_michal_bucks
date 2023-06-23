(function (){
    const ROOMS_TABLE_BODY_ID = "rooms-table-body";
    let roomsTableBodyElement;
    document.addEventListener("DOMContentLoaded", ()=>{
        roomsTableBodyElement = document.getElementById(ROOMS_TABLE_BODY_ID);

        subscribe();
    });

    function setRoomsTable(roomsJson){
        //TODO validate answer.
        roomsTableBodyElement.innerHTML = "";
        roomsJson.forEach((room) => {
            console.log(room)
            roomsTableBodyElement.innerHTML += `
                <tr class="table-secondary">
                    <td>${room.option}</td>
                    <td>asd</td>
                    <td>
                        <button type="button" class="btn btn-outline-danger">Enter</button>
                    </td>
                </tr>`;
        })
    }

    async function subscribe() {
        try {
            let response = await fetch("/lobby/test");
            if (response.status === 502) {
                // Status 502 is a connection timeout error,
                // may happen when the connection was pending for too long,
                // and the remote server or a proxy closed it
                // let's reconnect
                await new Promise(resolve => setTimeout(resolve, 10000));
                await subscribe();
            } else if (response.status !== 200) {
                // An error - let's show it
                console.log(response.statusText);
                // Reconnect in one second
                await new Promise(resolve => setTimeout(resolve, 10000));
                await subscribe();
            } else {
                // Get and show the message
                let message = await response.json();
                console.log(message);
                // Call subscribe() again to get the next message
                setRoomsTable(message);
                await new Promise(resolve => setTimeout(resolve, 10000));
                await subscribe()
            }
        }catch (e){
            console.log(e)
            await new Promise(resolve => setTimeout(resolve, 10000));
            await subscribe()
        }
    }
})();