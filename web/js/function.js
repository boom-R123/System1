var Start = [39.915168, 116.403875];//北京市中心
var End;
var overViewZoom = 8;
var $map = null;
var tilesOnline = L.tileLayer('http://{s}.tile.osm.org/{z}/{x}/{y}.png?{foo}', {foo: 'bar'});
var Lines;//出租车的线路
var i = 0;//for循环使用
var Markers = []//标记数组
var num = [];//每条移动对象点的数量
var now;//现在要画的轨迹号

var LeafIcon = L.Icon.extend({
    options: {
        shadowUrl: 'https://cdnjs.cloudflare.com/ajax/libs/leaflet/0.7.7/images/marker-shadow.png',
        iconSize: [15, 30], // size of the icon
        shadowSize: [15, 30], // size of the shadow
        iconAnchor: [7.5, 30], // point of the icon which will correspond to marker's location
        popupAnchor: [-3, -76] // point from which the popup should open relative to the iconAnchor
    }
});
var greenIcon = new LeafIcon({iconUrl: 'img/marker-icon-green.png'}),
    redIcon = new LeafIcon({iconUrl: 'img/marker-icon-red.png'}),
    blueIcon = new LeafIcon({iconUrl: 'img/marker-icon-blue.png'}),
    goldIcon = new LeafIcon({iconUrl: 'img/marker-icon-gold.png'}),
    orangeIcon = new LeafIcon({iconUrl: 'img/marker-icon-orange.png'}),
    violetIcon = new LeafIcon({iconUrl: 'img/marker-icon-violet.png'}),
    blackIcon = new LeafIcon({iconUrl: 'img/marker-icon-black.png'}),
    greyIcon = new LeafIcon({iconUrl: 'img/marker-icon-grey.png'}),
    yellowIcon = new LeafIcon({iconUrl: 'img/marker-icon-yellow.png'});
var Icon = [greenIcon, redIcon, blueIcon, goldIcon, orangeIcon, violetIcon, blackIcon, greyIcon, yellowIcon];

function InitMap() {
    if ($map != null) {
        overViewZoom = $map.getZoom();
        $map.remove();
        $map = null;
    }
    $map = L.map('div_map', {
        center: Start,
        zoom: overViewZoom,
        layers: tilesOnline,
        minZoom: 8,
        maxZoom: 16,
    });
    L.control.scale().addTo($map);  //比例尺
}

// function DrawLine() {
//     var InfOut = {
//         "action": "test"
//     };
//     $.ajax({
//         type: "post",
//         url: "S1",
//         data: InfOut,
//         success: function (data) {
//             var infIn = JSON.parse(data);
//             Start = infIn.Cars[0].Lines[0];
//             num[0] = infIn.Cars[0].Nums;
//             InitMap();
//             const markerS = L.marker(Start).addTo($map);
//             markerS.bindTooltip('这是起点', {direction: 'left'}).openTooltip();
//             End = infIn.Cars[0].Lines[infIn.Cars[0].Nums - 1];
//             const markerE = L.marker(End).addTo($map);
//             markerE.bindTooltip('这是终点', {direction: 'left'}).openTooltip();
//             Lines = infIn.Cars[0].Lines;
//             const polyline = L.polyline(Lines,
//                 {color: 'red'}).addTo($map);
//             DrawA(0);
//         },
//         error: function (XMLHttpRequest, textStatus, errorThrown) {
//             alert(XMLHttpRequest.status);
//         },
//     });
// }

var Lines=[[39.915168, 116.403875],[39.9, 116.4],[40,117],[39.915168, 116.403875]]

function DrawLine() {
    Start = Lines[0];
    InitMap();
    var polyline = L.polyline(Lines, { color: 'red' }).addTo($map);
    console.log(Lines)
    var marker2 = L.Marker.movingMarker(Lines,
        10000, {autostart: true}).addTo($map);
    marker2.setIcon(yellowIcon);
}
function MoveMarkers() {
    if (i < num[now]) {
        Markers[now].setLatLng(Lines[i]);
        setTimeout(" MoveMarkers();", 200);
        i++;
    } else {
        return;
    }
}

function DrawA(e) {//e为轨迹号
    i = 0;
    now = e;
    Markers[0] = L.marker(Start).addTo($map);
    MoveMarkers();//控制移动
}

function rad(d) {
    return d * Math.PI / 180.0;
}

function getDistance(e1, e2)//由经纬度转换成距离（米）
{
    var MLonA = e1[1];
    var MLatA = e1[0];
    var MLonB = e2[1];
    var MLatB = e2[0];
    var R = 6371004;
    var C = Math.sin(rad(e1[0])) * Math.sin(rad(e2[0])) + Math.cos(rad(e1[0])) * Math.cos(rad(e2[0])) * Math.cos(rad(MLonA - MLonB));
    return (R * Math.acos(C));
}


function DrawPoiArea() {
    var InfOut = {
        "action": "DrawArea"
    };
    $.ajax({
        type: "post",
        url: "S1",
        data: InfOut,
        success: function (data) {
            var infIn = JSON.parse(data);
            console.log(infIn)
            Start = infIn.clusters[0].Clusters[0];
            InitMap();
            var n = infIn.clusters.length;//簇的个数
            console.log(n);
            var x = -1;//控制使用什么图标
            for (i = 0; i < n; i++) {
                if (infIn.clusters[i].Clusters.length < 50)
                    continue;
                x++;
                for (j = 0; j < infIn.clusters[i].Clusters.length; j++) {
                    var point = infIn.clusters[i].Clusters[j];
                    const markerS = L.marker(point, {icon: Icon[x % 9]}).addTo($map);//给地图添加各个点
                }
                console.log(x);
                console.log(infIn.clusters[i].Clusters.length);
            }

        },
        error: function (XMLHttpRequest, textStatus, errorThrown) {
            alert(XMLHttpRequest.status);
        },
    });
}

/*
function DrawPoiArea() {
    var InfOut = {
        "action": "DrawArea"
    };
    $.ajax({
        type: "post",
        url: "S1",
        data: InfOut,
        success: function (data) {
            var infIn = JSON.parse(data);
            console.log(infIn)
            Start=infIn.clusters[0].Clusters[0];
            InitMap();
            var Center=[0,0];//地图显示的中心点
            var n = infIn.clusters.length;//簇的个数
            console.log(n);
            var x=-1;//控制使用什么图标
            for (i = 0; i < n; i++) {
                if(infIn.clusters[i].Clusters.length<70)
                    continue;
                x++;
                var center = [0, 0];//中心点的坐标
                for (j = 0; j < infIn.clusters[i].Clusters.length; j++) {
                    var point = infIn.clusters[i].Clusters[j];
                    center[0] = center[0] + point[0];
                    center[1] = center[1] + point[1];
                    const markerS = L.marker(point,{icon: Icon[x%9]}).addTo($map);//给地图添加各个点
                }
                console.log(x);
                center[0] = center[0] / infIn.clusters[i].Clusters.length;
                center[1] = center[1] / infIn.clusters[i].Clusters.length;
                Center[0] = Center[0] + center[0];
                Center[1] = Center[1] + center[1];

                //const markerS = L.marker(center,{icon: blueIcon}).addTo($map);
                //markerS.bindTooltip('这是簇的中心点', {direction: 'left'}).openTooltip();
                var dis=0;
                for (j = 0; j < infIn.clusters[i].Clusters.length; j++) {
                    var point = infIn.clusters[i].Clusters[j];
                    var temp=getDistance(center,point);
                    if(dis<temp){
                        dis=temp;
                    }
                }
                // const circle = L.circle(center, {
                //     color: 'green', //描边色
                //     fillColor: '#f03',  //填充色
                //     fillOpacity: 0.5, //透明度
                //     radius: dis+100 //半径，单位米
                // }).addTo($map);
                // circle.bindPopup('行政地标形成的簇,规模:'+infIn.clusters[i].size).openPopup();
            }
            Center[0] = Center[0] / infIn.clusters.length;
            Center[1] = Center[1] / infIn.clusters.length;
        },
        error: function (XMLHttpRequest, textStatus, errorThrown) {
            alert(XMLHttpRequest.status);
        },
    });
}
 */
