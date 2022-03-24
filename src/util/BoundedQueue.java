package util;

public class BoundedQueue<E> {

  private Object data[];


  public BoundedQueue(int length) {
    data = new Object[length];
  }


  public void push(E e) {
    for (int i = data.length - 1; i > 0; i--)
      data[i] = data[i - 1];

    data[0] = e;
  }


  @SuppressWarnings("unchecked")
  public E get(int i) {
    return (E) data[i];
  }


  public int getLength() {
    return data.length;
  }
}
