import org.apache.commons.lang3.tuple.Pair;

import java.lang.ref.WeakReference;

public class TriangleTimeSpan {
    public int id;
    public boolean isHead;
    public int mts;
    public TriangleTimeSpan timeNextTri;
    public TriangleTimeSpan timePreTri;
    public TriangleTimeSpan TriNext;
    public TriangleTimeSpan TriPre;

    public TriangleTimeSpan(int id,boolean isHead,int mts)
    {
        this.id=id;
        this.isHead=isHead;
        this.mts=mts;
        this.timeNextTri=null;
        this.timePreTri=null;
        this.TriNext=null;
        this.TriPre=null;
    }

    public TriangleTimeSpan() {
    }

    public void clear(){
        this.TriPre=null;
        this.TriNext=null;
        this.timeNextTri=null;
        this.timePreTri=null;
    }

    public void deleteTimeSpan(TriangleTimeSpan triangleTimeSpan)
    {
        //先处理一个节点的上下，即同一时刻的节点
        if(triangleTimeSpan.timeNextTri!=null){//还不是最后一个
            triangleTimeSpan.timePreTri.timeNextTri=triangleTimeSpan.timeNextTri;
            triangleTimeSpan.timeNextTri.timePreTri=triangleTimeSpan.timePreTri;
        }else{
            triangleTimeSpan.timePreTri.timeNextTri=null;
        }
        //之后处理一个节点的左右，即同一个三角形的节点
        if(triangleTimeSpan.isHead){  //这个节点是一个mts区间的头节点,在删除的时候我们需要将一个区间的头尾一起删除
            //先处理头节点对应的尾结点的上下部分
            if(triangleTimeSpan.TriNext.timeNextTri!=null){
                triangleTimeSpan.TriNext.timePreTri.timeNextTri=triangleTimeSpan.TriNext.timeNextTri;
                triangleTimeSpan.TriNext.timeNextTri.timePreTri=triangleTimeSpan.TriNext.timePreTri;
            }else{
                triangleTimeSpan.TriNext.timePreTri.timeNextTri=null;
            }
            //之后处理头节点和尾结点
            if(triangleTimeSpan.TriNext.TriNext!=null){
                triangleTimeSpan.TriPre.TriNext=triangleTimeSpan.TriNext.TriNext;
                triangleTimeSpan.TriNext.TriNext.TriPre=triangleTimeSpan.TriPre;
            }else{
                triangleTimeSpan.TriPre.TriNext=null;
            }
            //删除这个头节点和它的尾节点
            triangleTimeSpan.TriNext.clear();
            triangleTimeSpan.clear();
        }
        if(!triangleTimeSpan.isHead){
            //先处理尾结点对应的头节点的上下部分
            if(triangleTimeSpan.TriPre.timeNextTri!=null){
                triangleTimeSpan.TriPre.timePreTri.timeNextTri=triangleTimeSpan.TriPre.timeNextTri;
                triangleTimeSpan.TriPre.timeNextTri.timePreTri=triangleTimeSpan.TriPre.timePreTri;
            }else{
                triangleTimeSpan.TriPre.timePreTri.timeNextTri=null;
            }
            //之后处理尾结点和头节点
            if(triangleTimeSpan.TriNext!=null){
                triangleTimeSpan.TriNext.TriPre=triangleTimeSpan.TriPre.TriPre;
                triangleTimeSpan.TriPre.TriPre.TriNext=triangleTimeSpan.TriNext;
            }else{
                triangleTimeSpan.TriPre.TriPre.TriNext=null;
            }
            //删除这个尾结点和它的头节点
            triangleTimeSpan.TriPre.clear();
            triangleTimeSpan.clear();
        }
    }

    @Override
    public String toString() {
        return "TriangleTimeSpan{" +
                "id=" + id +
                ", isHead=" + isHead +
                ", mts=" + mts +
                '}';
    }
}
