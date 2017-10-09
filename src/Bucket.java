/**
 * Created by yuxuanli on 10/7/17.
 */
public class Bucket {
    int size, pos;
    public Bucket(int size, int pos){
        this.size = size;
        this.pos = pos;
    }

    public void setPos(int pos) {
        this.pos = pos;
    }
    public int getPos() {
        return pos;
    }
    public void setSize(int size) {
        this.size = size;
    }
    public int getSize() {
        return size;
    }
}
