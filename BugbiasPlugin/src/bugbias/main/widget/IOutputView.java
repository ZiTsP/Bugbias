package bugbias.main.widget;

public interface IOutputView {

    public void print(Object obj);
    public void print(Object... objs);
    public void caution(Object obj);
    public void clear();
}
