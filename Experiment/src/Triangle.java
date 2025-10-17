import org.apache.commons.lang3.tuple.Pair;

import java.util.Arrays;
import java.util.Objects;

public class Triangle {
    public Integer id;
    public boolean flag;
    public String e1;
    public String e2;
    public String e3;
    public TriangleTimeSpan timeSpanHead;

    public Triangle(int id,int e1,int e2,int e3){
        this.id = id;
        this.flag = true;
        this.e1 = (e1 +"_"+ e2);
        this.e2 = (e1 +"_"+ e3);
        this.e3 = (e2 +"_"+ e3);
        this.timeSpanHead =new TriangleTimeSpan(-1,true,-1);
    }

    public Triangle(int id,String e1,String e2,String e3){
        this.id = id;
        this.flag = true;
        this.e1 = e1;
        this.e2 = e2;
        this.e3 = e3;
        this.timeSpanHead =new TriangleTimeSpan(-1,true,-1);
    }

    public void addHeadTriangleTimeSpan(TriangleTimeSpan triangleTimeSpan){
        if (this.timeSpanHead.TriNext == null){
            this.timeSpanHead.TriNext=triangleTimeSpan;
            triangleTimeSpan.TriPre=this.timeSpanHead;
        }else{
            this.timeSpanHead.TriNext.TriPre=triangleTimeSpan;
            triangleTimeSpan.TriNext=this.timeSpanHead.TriNext;
            triangleTimeSpan.TriPre =this.timeSpanHead;
            this.timeSpanHead.TriNext = triangleTimeSpan;
        }
    }

    public void addTailTriangleTimeSpan(TriangleTimeSpan triangleTimeSpan){
        if (this.timeSpanHead.TriNext.TriNext == null){
            this.timeSpanHead.TriNext.TriNext=triangleTimeSpan;
            triangleTimeSpan.TriPre=this.timeSpanHead.TriNext;
        } else{
            this.timeSpanHead.TriNext.TriNext.TriPre=triangleTimeSpan;
            triangleTimeSpan.TriNext=this.timeSpanHead.TriNext.TriNext;
            triangleTimeSpan.TriPre =this.timeSpanHead.TriNext;
            this.timeSpanHead.TriNext.TriNext = triangleTimeSpan;
        }
    }

    public void deleteTriangleTimeSpan(int mts){
        if (this.timeSpanHead.TriNext == null){
            return;
        }
//        if(this.timeSpanHead.TriNext.mts>mts){
//            //如果这个三角形的最小的mts值比所要求的mts值要大，则这个三角形可以删除了
//            while (this.timeSpanHead.TriNext != null){
//                this.timeSpanHead.TriNext.deleteTimeSpan(this.timeSpanHead.TriNext);
//            }
//        }
        TriangleTimeSpan tts =this.timeSpanHead.TriNext;
        while (tts!=null){
            if (tts.mts>mts){
                TriangleTimeSpan tts2=tts.TriNext.TriNext;
                tts.deleteTimeSpan(tts);
                tts=tts2;
            }else{
                tts=tts.TriNext.TriNext;
            }
        }
    }

    @Override
    public boolean equals(Object o){
        if(o instanceof Triangle triangle){
            return triangle.e1 == this.e1 && triangle.e2 == this.e2 && triangle.e3 == this.e3;
        }
        return false;
    }
    @Override
    public int hashCode(){
        return  Objects.hash(e1,e2,e3);
    }
}
