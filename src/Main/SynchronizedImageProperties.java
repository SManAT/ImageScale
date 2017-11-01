package Main;

public class SynchronizedImageProperties {
  private long size=0;
  private long calculatedsize=0;
  private int anzahl=0;

  synchronized public long getSize() {
    return size;
  }

  synchronized public void addSize(long size) {
    this.size += size;
  }

  synchronized public long getCalculatedsize() {
    return calculatedsize;
  }

  synchronized public void addCalculatedsize(long calculatedsize) {
    this.calculatedsize += calculatedsize;
  }

  public int getAnzahl() {
    return anzahl;
  }

  public void setAnzahl(int anzahl) {
    this.anzahl = anzahl;
  }
  
  
}
