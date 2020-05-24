
function btmBindMain() {  //给按钮绑定函数
    $("#btm_ShowTrack").click(btm_ShowTrack);
    $("#btm_ReDrawMap").click(btm_ReDrawMap);
}


function btm_ShowTrack() {
    DrawLine();
}
function btm_ReDrawMap() {
    InitMap();
}