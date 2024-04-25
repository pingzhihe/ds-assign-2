package whiteBoard;


public interface WhiteBoardEventListener {
    void onDraw(String message);

    void onImg(byte[] img);
}
