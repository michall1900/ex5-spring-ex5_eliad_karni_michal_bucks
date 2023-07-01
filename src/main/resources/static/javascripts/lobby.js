(function (){
    // The id of the rooms table at the lobby html file.
    const ROOMS_TABLE_BODY_ID = "rooms-table-body";
    // The id of the error message element.
    const ERROR_MESSAGE_ID = "error-message";
    // Global variable that contains the rooms table element after the page load.
    let roomsTableBodyElement;
    // Global variable that contains the error message's element after the page load.
    let errorMessageElement;
    // The amount of seconds between rooms updates fetches (polling).
    const POLLING_RATE = 1;

    // At the event listener, sets the roomTableBody element and starting the polling recursion.
    document.addEventListener("DOMContentLoaded", ()=>{
        roomsTableBodyElement = document.getElementById(ROOMS_TABLE_BODY_ID);
        errorMessageElement = document.getElementById(ERROR_MESSAGE_ID);

        subscribe();
    });

    /**
     * The function fills the page's room table with the received rooms from the server.
     * In case received invalid response from the server, an informative error message will be shown to the user.
     * @param roomsJson The server response.
     */
    function setRoomsTable(roomsJson){
        try {
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
                errorMessageElement.innerHTML = '';
            })
        }catch (e){
            errorMessageElement.innerHTML = 'Failed to connect the server, trying to reconnect.'
        }
    }

    /**
     * The function fetching from the server the rooms list each POLLING_RATE's value amount of seconds.
     * @returns {Promise<void>} Got no value.
     */
    async function subscribe() {
        try {
            let response = await fetch("/lobby/getRooms");
            if (response.status === 502) {
                // Status 502 is a connection timeout error,
                // may happen when the connection was pending for too long,
                // and the remote server or a proxy closed it.
                errorMessageElement.innerHTML = 'Failed to connect the server, trying to reconnect.'
                await new Promise(resolve => setTimeout(resolve, POLLING_RATE * 1000));
                // trying to reconnect.
                await subscribe();
            } else if (response.status !== 200) {
                // An error - let's show it
                errorMessageElement.innerHTML = 'Failed to connect the server, trying to reconnect.'
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