package squid

import org.junit.Test
import MyBase._

class Basics {
  //val MyBase = new Base  // doesn't fix it
  //import MyBase._
  
  def foo(o0:Code[Int],o1:Code[Double]) = new Code[Double] { type Ctx = o0.Ctx with o1.Ctx }
  
  def foo0(o0:Code[Int],o1:Code[Double]): Code[Double] { type Ctx = o0.Ctx with o1.Ctx } = foo(o0,o1)
  
  def foo1(o0:Code[Int],o1:Code[Double]): CodeIn[Double, o0.Ctx with o1.Ctx] = foo(o0,o1)
  
  def foo2(o0:Code[Int],o1:Code[Double]): Code[Double] in o0.Ctx with o1.Ctx = foo(o0,o1) 
  // ^ Note: when Base is a class, doesn't work in Dotty only: Type argument squid.MyBase.Code[Double] does not conform to upper bound Base.this.Code[_]
  //         cf: https://github.com/lampepfl/dotty/issues/3591
  
  def bar[C](x0: Code[Int] in C, x1: Code[Double] in C) = foo(x0,x1)
  def bar0[C](x0: Code[Int] in C, x1: Code[Double] in C): Code[Double] in C = bar(x0,x1)
  def bar1[C](x0: CodeIn[Int,C], x1: CodeIn[Double,C]): CodeIn[Double,C] = bar0(x0,x1)
  
  @Test def t1(): Unit = {
     
    val a = mkCode[Int,{val s: String}]
    val b = mkCode[Double,{val b: Boolean}]
    
    foo(a,b)
    foo0(a,b)
    foo1(a,b)
    foo2(a,b)
    
    bar(a,b)
    bar0(a,b)
    bar1(a,b)
    
  }
  
  @Test def t2(): Unit = {
    
    val a = mkErasedCode[Int]
    val b = mkErasedCode[Double]
    
    foo(a,b)
    foo0(a,b)
    foo1(a,b)
    foo2(a,b)
    
    // These only work on Dotty!
    //bar(a,b)
    //bar0(a,b)
    //bar1(a,b)
    //bar(a,b):Code[Double] in D
    //bar0(a,b):Code[Double] in D
    //bar1(a,b):Code[Double] in D
    
    type D = a.Ctx with b.Ctx
    bar[D](a,b)
    bar0[D](a,b)
    bar1[D](a,b)
    
  }
  
  @Test def t3(): Unit = {
    
    foo(mkCode[Int,{val s: String}],mkCode[Double,{val b: Boolean}])
    
    bar(mkCode[Int,{val s: String}],mkCode[Double,{val b: Boolean}])
    
  }
  
  @Test def t4(): Unit = {
    
    foo(mkErasedCode[Int],mkCode[Double,{val b: Boolean}])
    
    // Only works on Dotty!
    //bar(mkErasedCode[Int],mkCode[Double,{val b: Boolean}])
    
  }
  
}

