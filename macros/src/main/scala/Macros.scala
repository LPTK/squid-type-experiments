import language.experimental.macros
import scala.reflect.macros.whitebox
import squid.MyBase._

object Macros {
  
  // Here is how to retrieve the context inside different code types
  
  def decomposeType[T]: String = macro decomposeTypeImpl[T]
  def decomposeTypeImpl[T:c.WeakTypeTag](c: whitebox.Context) = {
    import c.universe._
    val T = weakTypeOf[T]
    println(s"--- TYPE: $T ---")
    println(s"(( ${T.dealias} ))")
    
    //println(T.baseType(typeOf[in[_,_]].typeSymbol))
    //println(T.baseType(typeOf[CodeIn[_,_]].typeSymbol))
    println("BASE " + T.baseType(typeOf[Code[_]].typeSymbol))
    println("CTX " + (T.member(TypeName("Ctx")) match {
      case NoSymbol => "-"
      case sym => 
        //sym.asType.toType match {
        sym.typeSignature match { // <-------------------- here we match either a bounded abstract type or other types
          case TypeBounds(lb,ub) => s"$lb .. $ub"
          case tp =>
            //println(tp.getClass.getSuperclass)
            //println(tp.getClass.getInterfaces.toList.map(_.getSuperclass))
            //println(tp.getClass.getInterfaces.toList.flatMap(_.getInterfaces.toList.map(_.getSuperclass)))
            s"? $tp ${typeOf[{val x: Double}] <:< sym.asType.toType} ${sym.asType.toType <:< typeOf[{val x: Double}]}"
        }
    }))
    //println("TM " + (T.members.filter(_.isType)))
    println("TM " + (T.members.collectFirst{case sym:TypeSymbol if sym.name.toString == "Ctx" => 
      //s"$sym ${sym.asType.toType <:< typeOf[{val x: Double}]}"
      s"$sym ${sym.overrides}"
    }))
    
    //println("RF " + (T.dealias match { // note: needs dealias for it to work!
    //  case RefinedType(ts,scp) =>
    //    println(s"$ts $scp")
    //    scp.find(_.name==TypeName("Ctx")).map {
    //      case sym =>
    //        sym.asType
    //    }
    //  case _ => "?"
    //}))
    
    println
    
    q"identity(${Literal(Constant("ok"))})"
  }
  
}
