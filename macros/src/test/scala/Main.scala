import squid.MyBase._
import Macros._

object Main extends App {
  
  decomposeType[Int]
  decomposeType[Code[Int]]
  decomposeType[CodeIn[Int,{val x:Double}]]
  decomposeType[Code[Int] in {val x:Double}]
  decomposeType[Code[Int]{type Ctx = {val x:Double}}]
  
}
