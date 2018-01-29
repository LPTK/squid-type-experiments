## Prototyping the New Interface Types for Squid

### Goals

Nice/intuitive user interface, as close as possible to the papers

 * Should use types that all Scala compilers (Scala 2.11, 2.12 and Dotty) understand well; i.e., no existential crap

 * Handle higher-order code insertions  
 it's annoying, because the lambda that is to be spliced in should be of the type `(c:Code[T]) => Code[T] in c.Ctx` but currently only Dotty supports such dependent function types;  
 alternatively, if we have code"(x: Int) => ${f}(x)" we can accept `f` of type `Code[T] in {val x:Int} => Code[T] in {val x:Int}` 
 but there is no way to type `f` as such (guessing the name of the bound value) before it's even inserted (inserted stuff is typed before the QQ); it also poses the same problem with quasicode (which need a publicly-defined `$` function for insertion)  
 The only reasonable thing to do AFAICT is for the Scalac versions to accept a function of type `Code[T] => Code[T]` (which is obviously unsafe; could require an expicit import; we could also check that the context in the returned code is valid _after the fact_ in the QQ/QC macro)

 * Handle higher-order pattern variables; in particular extracted from `rewrite` (eventually, handle higher-order matching in general)  
 An extracted higher-order term should be associated with its own context, and this context woudl be used in the `apply` method of the higher-order hole.  
 Alternatively, we could leave the natural context for that hole, 
 but for the apply method use a context that corresponds to the dependent abstract context of the sub-holes.
 For example, in `case code"(x:Int) => $ctx(x+($n:Int))" =>`
 we extract `n: Code[Int]` 
 and `ctx: { type T; def apply(arg0: Code[Int] in n.Ctx): Code[T] in <outer ctx> }`
 


### Approach

`Code[T]` is the general code type, and its `Ctx` type member is refined as wanted; which can be done easily with the `in` type alias, as in `Code[Int] in {val x: Double}`.  

This is nice, because it means we can keep many method defintions simple,as in:

```scala
def foo(x: Code[Int], y: Code[String]) = code"$y * $x"
// ^ foo has type: (x: Code[Int], y: Code[String])Code[String] in x.Ctx & y.Ctx
foo(code"2", code"1.toString") : ClosedCode[String] // ie: Code[Int] in Any
foo(code"2", code"?s:String")  : Code[String] in {val s: String}
```
 

### Aesthetics

the QQ macro should pick the best type synonym to use;
in particular so it can show well in the REPL.
for example:

```scala
> code"2.toDouble"
res: ClosedCode[Double] = ...

> code"(?n:Int).toDouble"
res: Code[Double]{type Ctx = {val n: Int}} = ...
// or:
res: CodeIn[Double,{val n: Int}] = ...
// or:
res: in[CodeIn[Double],{val n: Int}] = ...
// hopefully in future Scala we can specify to show infix types like `in` correctly... 
``` 


### Compiling and Testing

`sbt "+ test"` to compile and run the tests in Scala 2.11, 2.12 and Dotty

`sbt "++ 0.4.0-RC1"` to switch to Dotty



## Things to Keep in Mind / Take Into Consideration

### Tight `Typ` Type and Type Representation Evidence

The situation sometimes arises where we have a `cde: Code[T,C]` with no type evidence for `T` in scope, and propagating that evidence may not always be easy/feasible. If we compose `cde` into a bigger program, as in `code"($cde,1)"` Squid will complain about the lack of evidence.

In Squid 0.x, we have to do contorsions to make it work: `code"(${cde.ofUnderlyingTyp} : ${cde.Typ}, 1)"` where `ofUnderlyingTyp` yields a `Code[cde.Typ,C]` and inserting `cde.Typ` is necessary to let Squid find the `CodeType[cde.Typ]` evidence. The call to `ofUnderlyingTyp` is necessary because Sala can't know (with good reason) that `T =:= cde.Typ` (or even that `T <: cde.Typ`) since `Code[_,C]` is covariant.

Instead, it would be better that the quasiquote macro assign inserted terms the tight type by default (if it has a proper singleton representation; otherwise for simplicity fall back to `T`), and be able to retrieve that type representation automatically from the inserted term.


