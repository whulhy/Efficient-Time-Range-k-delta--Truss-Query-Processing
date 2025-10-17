import java.io.IOException;

public class ShearTimeList {
    public String [][]timelist;
    public int ts;
    public int te;
    public int n;
    public ShearTimeList(int ts,int te)
    {
        this.ts=ts;
        this.te=te;
        this.n=te-ts+1;
        this.timelist= new String[n][n];
    }

    public void init(){
        for (int i=0;i<n;i++){
            for (int j=0;j<n-i;j++){
                timelist[i][j]="0";
            }
        }
    }



    public void Shear(int ts,int te,int tts,int tte){
        if (tte<te){
            for (int c=te-1;c>=tte;c--){
                setElement(ts,c,"1");
            }
        }
        if(tts>ts){
            for (int r=ts+1;r<=tts;r++){
                for(int c=te;c>=r;c--){
                    setElement(r,c,"1");
                }
            }
        }
        if (tts>ts&&tte<te){
            for (int r=tts+1;r<=tte;r++){
                for (int c=te;c>=tte+1;c--){
                    setElement(r,c,"1");
                }
            }
        }
    }

    public String getElement(int i,int j)
    {
        if (j < i) {
            throw new IllegalArgumentException("Index out of upper triangular bounds");
        }
        return timelist[i-ts][te-j];
    }
    public void setElement(int i,int j,String s){
        timelist[i-ts][te-j]=s;
    }
    public void print(){

    }
}
