window.onload = init;
var socket = new WebSocket("ws://localhost:8080/WebsocketHome/actions");
socket.onmessage = onMessage;

var playerMoney = 0;
var properties = '';
var notEnoughMoney = false;

// only displays the add player part at start, hides main interface
function init() {
    showAddPlayerForm();
    document.getElementById("interfaceWrapper").style.display = 'none';
    document.getElementById("homeWrapper").style.display = '';
}

// reads JSON and does things based on message.
function onMessage(event) {
    
    var player = JSON.parse(event.data);

    // adds you as a player with key info on top right.
    if (player.action === "add") {
        playerMoney = player.money;
        printPlayerElement(player);
        document.getElementById('startGame').style.display = '';
    }

    // removes you from the game.
    if (player.action === "remove") {
        document.getElementById(player.id).remove();
        removePlayer(player.id);
    }

    // adds message to log on right of screen.
    if (player.action === "log") {
        var log = document.getElementById('logDisplay');
        var line = document.createElement("P");
        line.appendChild(document.createTextNode(player.message));
        line.appendChild(document.createElement("br"));
        log.appendChild(line);
//        updateScroll();
    }

//  updates player's money and position.
    if (player.action === "updateInfo"){
        playerMoney = player.money;
        document.getElementById("Money").innerHTML = playerMoney;
        document.getElementById("Position").innerHTML = player.position;
        checkIfCanBuy();
    }

    // displays information on card next to board about property that the user clicked.
    if (player.action === "sendPropertyTileInfo"){
        var cardInfo = document.createElement("div");
        cardInfo.setAttribute("class", "cardInfo");
        cardInfo.innerHTML = "<h4>" + player.name +" </h4>";
        if (player.owner !== ""){
            cardInfo.innerHTML += "Owner: " + player.owner + "<br/>";
        }
        else{
            cardInfo.innerHTML +=  "Owner: Unowned<br/>Cost: " + player.cost + "<br/>";
        }
        cardInfo.innerHTML += "Rent: " + player.rent
        document.getElementById("card").innerHTML = "";
        document.getElementById("card").appendChild(cardInfo);
    }
        
        // displays information on card next to board about tile 
    // (that is not a property) that the user clicked.
    if (player.action === "sendTileInfo"){
        var cardInfo = document.createElement("div");
        cardInfo.setAttribute("class", "cardInfo");
        cardInfo.innerHTML = "<h4>" + player.name + "</h4>";
        document.getElementById("card").innerHTML = "";
        document.getElementById("card").appendChild(cardInfo);
    }

    if (player.action === "roll") {
        // document.getElementById('rollDice').style.display = '';
        // document.getElementById('endTurn').style.display = '';
        document.getElementById("Position").innerHTML = player.position;
    }
    
//  Allows player to bid by displaying bidding input and changing the minimum bid to the
//  current bid.
    if (player.action === "auction") {
        document.getElementById("auctionBid").setAttribute("min", player.currentBid);
        document.getElementById("currentBid").innerHTML = player.currentBid;
        document.getElementById('AuctionBox').style.display = '';
        
    }
    
//  Gives player the option to buy property but hides it if they don't have enough money.
    if (player.action === "buyOption") {
        document.getElementById('PurchaseProperty').style.display = '';
        playerMoney = player.playerMoney;
        var propertyCost = player.propertyCost;
        if (playerMoney < propertyCost){
            document.getElementById('buyPropertyButton').style.display = 'none';
            document.getElementById('buyPropertyOption').innerHTML += 'Not enough money, sell property or pass.';
            notEnoughMoney = true;
        }
    }
    
    if (player.action === "buy") {
        playerMoney = player.playerMoney;
        document.getElementById("Money").innerHTML = playerMoney;
        // properties += player.propertyName + "<a href=\"#\" OnClick=sellProperty(" + player.id + ")> Sell Property</a>";
        // document.getElementById("Property").innerHTML = "[" + properties + "]";
        printPropertyElement(player);
    }

    // updates money after paying rent.
    if (player.action === "payRent") {
        playerMoney -= player.propertyCost;
        document.getElementById("Money").innerHTML = playerMoney;
    }
    
    // updates money after receiving rent.
    if (player.action === "receieveRent") {
        playerMoney += player.propertyCost;
        document.getElementById("Money").innerHTML = playerMoney;
        checkIfCanBuy();
    }
  
    if (player.action === "cardMove"){
        document.getElementById("Position").innerHTML = player.position;
    }
    if (player.action === "cardPay"){
        playerMoney = player.money;
        document.getElementById("Money").innerHTML = playerMoney;
    }
    if (player.action === "cardCollect"){
        playerMoney = player.money;
        document.getElementById("Money").innerHTML = playerMoney;
        checkIfCanBuy();
    }

    else{
        document.getElementById('NotificationBox').style.display = 'none';
    }
}


 function startGame() {
     var PlayerAction = {
         action: "start"
     };
     socket.send(JSON.stringify(PlayerAction));
     document.getElementById("startGame").style.display = 'none';
 }


//add player, sends their name to WebSocket.
function addPlayer(name) {
    var PlayerAction = {
        action: "add",
        name: name
    };
    socket.send(JSON.stringify(PlayerAction));
    document.getElementById("homeWrapper").style.display = 'none';
    document.getElementById("interfaceWrapper").style.display = '';
}

//removes player, sends their id to WebSocket.
function removePlayer(element) {
    var id = element;
    var PlayerAction = {
        action: "remove",
        id: id
    };
    socket.send(JSON.stringify(PlayerAction));
    document.getElementById("homeWrapper").style.display = '';
    document.getElementById("interfaceWrapper").style.display = 'none';
    showAddPlayerForm();
}

// Buys Property for its set price.
function buyProperty(){
    var PlayerAction = {
        action: "buy"
    };
    socket.send(JSON.stringify(PlayerAction));
    document.getElementById("PurchaseProperty").style.display = 'none';
//    hideForm(this);
}

// Does not buy Property, thus it goes up for auction.
function passOnProperty(){
    var PlayerAction = {
        action: "pass"
    };
    socket.send(JSON.stringify(PlayerAction));
    document.getElementById("PurchaseProperty").style.display = 'none';
    notEnoughMoney = false;
//    hideForm(this);
}

// Submits bid for property during auction.
function submitBid(elem){
    var form = document.getElementById("BidYesNo");
    var amount = parseInt(form.elements["auctionBid"].value);
//    var amount = document.getElementById("auctionBid").value;
    var PlayerAction = {
        action: "bid",
        amount: amount
    };
    socket.send(JSON.stringify(PlayerAction));
    document.getElementById("AuctionBox").style.display = 'none';
    document.getElementById("auctionBid").value = 0;
}

// Does not make another bid in auction.
function passOnAuction(element){
    var PlayerAction = {
        action: "passAuction",
        amount: 0
    };
    socket.send(JSON.stringify(PlayerAction));
    document.getElementById("AuctionBox").style.display = 'none';
}

// Pays fee set to it, e.g: Tax, Rent, Chance Card.
function payFee(element){
    var PlayerAction = {
        action: "pay"
    };
    socket.send(JSON.stringify(PlayerAction));
}



// Sells own property, gives position.
function sellProperty(position, propertyId){
    var PlayerAction = {
        action: "sell",
        position: position
    };
    document.getElementById(propertyId).parentNode.removeChild(document.getElementById(propertyId));
    socket.send(JSON.stringify(PlayerAction));
}

// rolls dice button. NOT IN USE.
function rollDice(){
    var PlayerAction = {
        action: "roll"
    };
    socket.send(JSON.stringify(PlayerAction));
    document.getElementById('rollDice').style.display = 'none';
}

// end turn button. NOT IN USE.
function endTurn(){
    var PlayerAction = {
        action: "end"
    };
    document.getElementById('NotificationBox').style.display = '';
    document.getElementById('endTurn').style.display = 'none';
}


function showForm(elem){
    elem.style.display = "";
}

//shows addPlayerForm
function showAddPlayerForm() {
    document.getElementById("addPlayerForm").style.display = '';
}

// hides addPlayerForm
function hideAddPlayerForm() {
    document.getElementById("addPlayerForm").style.display = "none";
}


// hides original add player  adds player to game.
function formSubmit() {
    var form = document.getElementById("addPlayerForm");
    var name = form.elements["player_name"].value;
    hideAddPlayerForm();
    addPlayer(name);
}

// adds information to the user's stats on top right when they join the game.
function printPlayerElement(player) {
    var stats = document.getElementById("stats");
    document.getElementById("Name").innerHTML = player.username;
    document.getElementById("Id").innerHTML = player.id;
    document.getElementById("Money").innerHTML = playerMoney;
    document.getElementById("Position").innerHTML = player.position;

    var removePlayer = document.createElement("span");
    removePlayer.setAttribute("class", "removePlayer");
    removePlayer.innerHTML = "<a href=\"#\" OnClick=removePlayer(" + player.id + ")>Leave Game</a>";
    stats.appendChild(removePlayer);
}

// adds list of player owned properties to the bottom of the screen.
function printPropertyElement(property) {
    var listOfProperties = document.getElementById("listOfProperties");

    var propertyDiv = document.createElement("div");
    propertyDiv.setAttribute("id", property.propertyID);
    propertyDiv.setAttribute("class", "property"); //" + property.color"? for displaying color group? specific tag in css
    listOfProperties.appendChild(propertyDiv);

    var propertyName = document.createElement("span");
    propertyName.setAttribute("class", "propertyName");
    propertyName.innerHTML = property.propertyName;
    propertyDiv.appendChild(propertyName);

    var mortgageStatus = document.createElement("span");
    if (property.mortgageStatus === "True") {
//        playerStatus.innerHTML = "<b>Status:</b> " + player.status + " (<a href=\"#\" OnClick=togglePlayer(" + player.id + ")>Turn off</a>)";
    } else if (property.status === "False") {
//        playerStatus.innerHTML = "<b>Status:</b> " + player.status + " (<a href=\"#\" OnClick=togglePlayer(" + player.id + ")>Turn on</a>)";
        //playerDiv.setAttribute("class", "player off");
    }
    propertyDiv.appendChild(mortgageStatus);

    var sellProperty = document.createElement("span");
    sellProperty.setAttribute("class", "sellProperty");
    sellProperty.innerHTML = " - <a href=\"#\" OnClick=sellProperty(" + property.propertyPosition + "," + property.propertyID + ")>Sell this Property</a>";
    propertyDiv.appendChild(sellProperty);
}

// sends message into log and makes the message box empty.
function sendMessage(){
    var form = document.getElementById("chat");
    var message = form.elements["player_chat"].value;
    var PlayerAction = {
        action: "chat",
        message: message
    };
    socket.send(JSON.stringify(PlayerAction));
    form.elements["player_chat"].value = '';
}

// It receives a tile position sent from an onclick on the image map and sends that to
// the server so that the gamesessionhandler can send back information regarding this tile.
function sendForCardInfo(elem){
    var PlayerAction = {
        action: "cardInfo",
        position: parseInt(elem.getAttribute("data-tile-type"))
    };
    socket.send(JSON.stringify(PlayerAction));
}

// checks player's money after they receive more to see if they can buy property.
function checkIfCanBuy(){
    if (notEnoughMoney){
        if (playerMoney >= propertyCost){
            document.getElementById('buyPropertyButton').style.display = 'none';
            document.getElementById('buyPropertyOption').innerHTML = '';
        }
    }
}
