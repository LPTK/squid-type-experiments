package squid

//class Base {
object MyBase { // cf: https://github.com/lampepfl/dotty/issues/3591
  
  class ErasedCode { type Typ }
  class Code[+T] extends ErasedCode {
    type Ctx
    type Typ <: T
  }
  type CodeIn[+T,-C] = Code[T] { type Ctx >: C }
  //type in[+Cde <: Code[_], -C] = Cde { type Ctx >: C }
  type in[+Cde <: ErasedCode, -C] = Cde { type Ctx >: C }
  type ClosedCode[+T] = Code[T] in Any
  
  def mkCode[T,C]: Code[T] in C = new Code[T] { type Ctx = C }
  def mkErasedCode[T]: Code[T] = new Code[T]
  
  
}
