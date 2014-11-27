angular.module('npApp', [])

.controller('NpCtrl', ['$scope', function($scope){
    $scope.data = {count: 0, text: "hello"};
    $scope.cellMap = [
        [{id:1, val:1, style:'cell', x:0, y:0}, {id:2, val:2, style:'cell', x:0, y:1}, {id:3, val:3, style:'cell', x:0, y:2}, {id:4, val:4, style:'cell', x:0, y:3}],
        [{id:5, val:5, style:'cell', x:1, y:0}, {id:6, val:6, style:'cell', x:1, y:1}, {id:7, val:7, style:'cell', x:1, y:2}, {id:8, val:8, style:'cell', x:1, y:3}],
        [{id:9, val:9, style:'cell', x:2, y:0}, {id:10, val:10, style:'cell', x:2, y:1}, {id:11, val:11, style:'cell', x:2, y:2}, {id:12, val:12, style:'cell', x:2, y:3}],
        [{id:13, val:13, style:'cell', x:3, y:0}, {id:14, val:14, style:'cell', x:3, y:1}, {id:15, val:15, style:'cell', x:3, y:2}, {id:16, style:'empty-cell', x:3, y:3}]
    ];
    $scope.numSteps = function() {
        return $scope.data.count;
    };
    $scope.textMessage = function() {
        return $scope.data.text;
    }
    $scope.swap = function(e1, e2) {
        elemX = e1.attr("data-row");
        elemY = e1.attr("data-col");
        emptyX = e2.attr("data-row");
        emptyY = e2.attr("data-col");

        //swap
        var t0 = $scope.cellMap[emptyX][emptyY].id;
        var t1 = $scope.cellMap[emptyX][emptyY].val;
        var t2 = $scope.cellMap[emptyX][emptyY].style;
        $scope.cellMap[emptyX][emptyY].id = $scope.cellMap[elemX][elemY].id;
        $scope.cellMap[emptyX][emptyY].val = $scope.cellMap[elemX][elemY].val;
        $scope.cellMap[emptyX][emptyY].style = $scope.cellMap[elemX][elemY].style;
        $scope.cellMap[elemX][elemY].id = t0;
        $scope.cellMap[elemX][elemY].val = t1;
        $scope.cellMap[elemX][elemY].style = t2;

        $scope.$apply();

        //adjust transform variables for draggables
        // e1.css("z-index", 0);
        // e2.css("z-index", -1);
        e1.css("transform","translate3d(0px, 0px, 0px)");
        e2.css("transform","translate3d(0px, 0px, 0px)");
        Draggable.get(e1)._eventTarget._gsTransform.x = 0;
        Draggable.get(e1)._eventTarget._gsTransform.y = 0;
        Draggable.get(e2)._eventTarget._gsTransform.x = 0;
        Draggable.get(e2)._eventTarget._gsTransform.y = 0;
        // e1.css("z-index", -1);
    }

    /*$scope.hello = function($event) {
        console.log($event);
    }*/

    $scope.shuffle = function() {
        function ns() {
            function generateTransform(n1, n2) {
                var offset1 = n1.offset();
                var offset2 = n2.offset();
                var deltaXCell = offset2.left - offset1.left;
                var deltaXNext = 0 - deltaXCell;
                var deltaYCell = offset2.top - offset1.top;
                var deltaYNext = 0 - deltaYCell;
                return {
                    tCell: "translate3d(" + deltaXCell + "px, " + deltaYCell + "px, 0px)",
                    tNext: "translate3d(" + deltaXNext + "px, " + deltaYNext + "px, 0px)"
                }
            }

            var count = 0
            function work() {
                var i = Math.floor(Math.random() * 4);
                var j = Math.floor(Math.random() * 4);
                var e = $("#" + $scope.cellMap[i][j].id);
                var f, t;

                var ii, jj;
                do {
                    var ii = Math.floor(Math.random() * 4);
                    var jj = Math.floor(Math.random() * 4);
                } while(ii === i && jj === j)

                f = $("#" + $scope.cellMap[ii][jj].id);
                t = generateTransform(e, f);

                new TimelineMax().to(e, 0.3, {transform:t.tCell});
                new TimelineMax().to(f, 0.3, {transform:t.tNext, onComplete:function() {
                    $scope.swap(e, f);
                    if(++count < 30) {
                        work();
                    }
                }});
            }
            work();
        }
        
        ns();
    }
}])

.directive('npCell', [function() {
    return {
        restrict: 'A', // E = Element, A = Attribute, C = Class, M = Comment
        link: function(scope, iElm, iAttrs, controller) {
            // var emptyCell = $(".empty-cell");
            var overlapThreshold = '45%';

            Draggable.create(iElm, {
                bounds:$("#gameArea"),
                edgeResistance:0.8,
                type:"x,y",
                lockAxis:true,
                onDragEnd:function(event) {
                    var emptyCell = $(".empty-cell"); //TODO: Selector major performance hit - fix
                    if(this.hitTest(emptyCell, overlapThreshold)) {
                        scope.data.count += 1;
                        scope.data.text += "-no-";

                        scope.swap(iElm, emptyCell);
                    } else { //Restore element back to its original cell since it could have been dragged anywhere
                        iElm.css("-webkit-transform", "translate3d(0px, 0px, 0px)");
                        iElm.css("transform", "translate3d(0px, 0px, 0px)");
                        this._eventTarget._gsTransform.x = 0;
                        this._eventTarget._gsTransform.y = 0;
                    }
                }
            });

           /* iElm.bind("keypress", function() {
                console.log("key pressed");
            })*/
        }
    }
}])

.directive('npGame', ['$document', 'gameService', function($document, gameService) {
    var wip = false;
    return {
        restrict: 'A',
        link: function(scope, iElm, iAttrs, controller){
            $document.on("keypress", function($event) {
                if(wip) {
                    $event.preventDefault();
                    return;
                }
                wip = true;
                var key = $event.which;
                if(key === 108) {//l
                    var emptyCell = $("#16");
                    var e = emptyCell.prev();
                    var eOffset = e.offset();
                    if(eOffset === undefined || eOffset === null) {
                        return;
                    }
                    var xTrans = gameService.generateXTransform(eOffset, emptyCell.offset());
                    TweenMax.to(emptyCell, 0.5, {transform: "translate3d(" + xTrans.deltaXNext + "px, 0px, 0px)"});
                    TweenMax.to(e, 0.5, {transform:"translate3d(" + xTrans.deltaXCell + "px, 0px, 0px)", onComplete:function() {
                        scope.swap(e, emptyCell);
                        wip = false;
                    }});
                } else if(key === 104) {//h
                    var emptyCell = $("#16");
                    var e = emptyCell.next();
                    var eOffset = e.offset();
                    if(eOffset === undefined || eOffset === null) {
                        return;
                    }
                    var xTrans = gameService.generateXTransform(eOffset, emptyCell.offset());
                    TweenMax.to(e, 0.5, {transform: "translate3d(" + xTrans.deltaXCell + "px, 0px, 0px)"});
                    TweenMax.to(emptyCell, 0.5, {transform:"translate3d(" + xTrans.deltaXNext + "px, 0px, 0px)", onComplete:function() {
                        scope.swap(e, emptyCell);
                        wip = false;
                    }});
                } else if(key === 106) {//j
                    var emptyCell = $("#16");
                    var e = emptyCell.next();
                    var eOffset = e.offset();
                    if(eOffset === undefined || eOffset === null) {
                        return;
                    }
                    var yTrans = gameService.generateYTransform(eOffset, emptyCell.offset());
                    TweenMax.to(e, 0.5, {transform: "translate3d(0px, " + yTrans.deltaYCell + "px, 0px)"});
                    TweenMax.to(emptyCell, 0.5, {transform:"translate3d(0px, " + yTrans.deltaYNext + "px, 0px)", onComplete:function() {
                        scope.swap(e, emptyCell);
                        wip = false;
                    }});
                } else if(key === 107) {//k
                    var emptyCell = $("#16");
                    var e = emptyCell.next();
                    var eOffset = e.offset();
                    if(eOffset === undefined || eOffset === null) {
                        return;
                    }
                    var yTrans = gameService.generateYTransform(eOffset, emptyCell.offset());
                    TweenMax.to(e, 0.5, {transform: "translate3d(0px, " + yTrans.deltaYCell + "px, 0px)"});
                    TweenMax.to(emptyCell, 0.5, {transform:"translate3d(0px, " + yTrans.deltaYNext + "px, 0px)", onComplete:function() {
                        scope.swap(e, emptyCell);
                        wip = false;
                    }});
                }
            })
        }
    }
}])

.service('gameService', [function() {
    this.generateTransform = function(n1, n2) {
        var offset1 = n1.offset();
        var offset2 = n2.offset();
        var xTrans = generateXTransform(offset1, offset2);
        var yTrans = generateYTransform(offset1, offset2);
        return {
            tCell: "translate3d(" + xTrans.deltaXCell + "px, " + yTrans.deltaYCell + "px, 0px)",
            tNext: "translate3d(" + xTrans.deltaXNext + "px, " + yTrans.deltaYNext + "px, 0px)"
        }
    }

    this.generateXTransform = function(offset1, offset2) {
        var deltaXCell = offset2.left - offset1.left;
        var deltaXNext = 0 - deltaXCell;
        return {
            deltaXCell: deltaXCell,
            deltaXNext: deltaXNext
        };
    }

    this.generateYTransform = function(offset1, offset2) {
        var deltaYCell = offset2.top - offset1.top;
        var deltaYNext = 0 - deltaYCell;
        return {
            deltaYCell: deltaYCell,
            deltaYNext: deltaYNext
        };
    }
}])
