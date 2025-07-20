
class Part{
  String name;
  String type;
  float cost;
  int count;
    
  Part( String name, String type, float cost, int count ){
    this.name = name;
    this.count = count;
    this.type = type;
    this.cost = cost;
  }
  
  Part( String name, String type, int count ){
    this.name = name;
    this.count = count;
    this.type = type;
  }
  
  public String toString(){
    return "name: " + name + ", type: " + type + ", count: " + count;
  }
  
  
}
