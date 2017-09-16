var app = angular.module('chatApp', ['ngMaterial']);

app.controller('chatController', function ($scope)
{
	$scope.messages = [
		{sender:"BOT", text:"Hello, how may I help you?", time:"1:12pm"},
		{sender:"USER", text:"Can you find me the original paperback copy of Harry Potter Series?", time:"1:13pm"},
		{sender:"BOT", text:"Right on it.", time:"1:15pm"},
		{sender:"USER", text:"Thank you.", time:"1:18pm"}];

    var exampleSocket = new WebSocket("ws://localhost:9000/chatSocket");
    exampleSocket.onmessage = function (event)
    {
        var jsonData = JSON.parse(event.data);
        console.log(jsonData);
    }
});

