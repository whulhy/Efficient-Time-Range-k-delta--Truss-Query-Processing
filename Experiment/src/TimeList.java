public class TimeList {
    public int time;
    public TimeList nextTime;
    public TimeList preTime;
    public TriangleTimeSpan nextTriangle;

    public TimeList(int time,TimeList preTime,TimeList nextTime)
    {
        this.time=time;
        this.nextTime=nextTime;
        this.preTime=preTime;
        this.nextTriangle=new TriangleTimeSpan(-1,false,-1);
    }

    /**
     * 添加一个三角形时间跨度到链表中。
     * 此方法用于将一个新的三角形时间跨度对象链接到当前链表中。如果链表当前为空，
     * 则新对象成为链表的起始点；如果链表不为空，则新对象被插入到链表的末尾，
     * 并且确保链表的双向连接性质得以保持。
     *
     * @param triangleTimeSpan 待添加的三角形时间跨度对象，它将被链接到当前链表中。
     */
    public void addTriangle(TriangleTimeSpan triangleTimeSpan) {
        // 检查链表是否为空，如果是，则将新对象设置为链表的起始点
        if (this.nextTriangle.timeNextTri==null) {
            this.nextTriangle.timeNextTri=triangleTimeSpan;
            triangleTimeSpan.timePreTri=this.nextTriangle;
        } else {
            // 链表不为空时，将新对象插入到链表的头并保持双向连接
            this.nextTriangle.timeNextTri.timePreTri=triangleTimeSpan;
            triangleTimeSpan.timeNextTri=this.nextTriangle.timeNextTri;
            triangleTimeSpan.timePreTri=this.nextTriangle;
            this.nextTriangle.timeNextTri=triangleTimeSpan;
        }
    }
    public void deleteTime() {
        if (this.nextTime==null&&this.preTime==null){
            return;
        }
        else if(this.nextTime==null)  //如果这个时刻后面的节点为null，说明要删除的是最后一个节点
        {
            this.preTime.nextTime=null;
            this.preTime=null;
        }else if(this.preTime==null)  //如果是第一个时刻
        {
            this.nextTime.preTime=null;
            this.nextTime=null;
        }else {
            this.nextTime.preTime=this.preTime;
            this.preTime.nextTime=this.nextTime;
        }
    }

}
