package ast_visitors;

/**
 * CheckTypes
 *
 * This AST visitor traverses a MiniJava Abstract Syntax Tree and checks
 * for a number of type errors.  If a type error is found a SymanticException
 * is thrown
 *
 * CHANGES to make next year (2012)
 *  - make the error messages between *, +, and - consistent <= ??
 *
 * Bring down the symtab code so that it only does get and set Type
 *  for expressions
 */
/*
Functions Done:
defaultOut
outAndExp
outPlusExp
outMinusExp
outMulExp
outEqualExp
outNegExp
outByteCast
*/
import ast.node.*;
import ast.visitor.DepthFirstVisitor;
import java.util.*;
import ast.visitor.*;
import symtable.SymTable;
import symtable.Type;
import exceptions.InternalException;
import exceptions.SemanticException;

public class CheckTypes extends DepthFirstVisitor
{

   private SymTable mCurrentST;

   public CheckTypes(SymTable st) {
     if(st==null) {
          throw new InternalException("unexpected null argument");
      }
      mCurrentST = st;
   }
   public CheckTypes(){
       mCurrentST = new SymTable();
   }
   public SymTable getSymTable(){
       return mCurrentST;
   }

   //========================= Overriding the visitor interface
   @Override
   public void defaultOut(Node node) {
       System.err.println("Node not implemented in CheckTypes, " + node.getClass());
   }
   /*
   Most Boolean Expressions
   TrueExp
   FalseExp
   AndExp
   EqualExp
   NotExp*/
   //======================== Overriding the Bool Exp Nodes
   @Override
   public void outAndExp(AndExp node)
   {
     if(this.mCurrentST.getExpType(node.getLExp()) != Type.BOOL) {
       throw new SemanticException(
         "Invalid left operand type for operator &&",
         node.getLExp().getLine(), node.getLExp().getPos());
     }

     if(this.mCurrentST.getExpType(node.getRExp()) != Type.BOOL) {
       throw new SemanticException(
         "Invalid right operand type for operator &&",
         node.getRExp().getLine(), node.getRExp().getPos());
     }

     this.mCurrentST.setExpType(node, Type.BOOL);
   }
   @Override
   public void outBlockStatement(BlockStatement node)
   {
       this.mCurrentST.setExpType(node, Type.VOID);
   }
   @Override
   public void outNotExp(NotExp node){
       if(this.mCurrentST.getExpType(node.getExp()) != Type.BOOL) {
         throw new SemanticException(
           "Invalid left operand type for operator &&",
           node.getExp().getLine(), node.getExp().getPos());
       }
       else{
           this.mCurrentST.setExpType(node, Type.BOOL);
       }
   }

   /*
   Integer and Byte Expression nodes
   ByteCast
   IntegerExp
   MinusExp
   MulExp
   PlusExp
   NegExp*/
   //==========================Overriding the Integer and Byte Exp Nodes
   @Override
   public void outPlusExp(PlusExp node)
   {
       Type lexpType = this.mCurrentST.getExpType(node.getLExp());
       Type rexpType = this.mCurrentST.getExpType(node.getRExp());
       if ((lexpType==Type.INT  || lexpType==Type.BYTE) &&
           (rexpType==Type.INT  || rexpType==Type.BYTE)
          ){
           this.mCurrentST.setExpType(node, Type.INT);
       } else {
           throw new SemanticException(
                   "Operands to + operator must be INT or BYTE",
                   node.getLExp().getLine(),
                   node.getLExp().getPos());
       }

   }
   @Override
   public void outMinusExp(MinusExp node){
       Type lexpType = this.mCurrentST.getExpType(node.getLExp());
       Type rexpType = this.mCurrentST.getExpType(node.getRExp());
       if ((lexpType==Type.INT || lexpType==Type.BYTE) &&
           (rexpType == Type.INT || rexpType==Type.BYTE)
          ){
            this.mCurrentST.setExpType(node, Type.INT);

       }
       else{
           throw new SemanticException(
                "Operands to - operator must be INT OR BYTE",
           node.getLExp().getLine(),
           node.getLExp().getPos());
       }
   }
   @Override
   public void outMulExp(MulExp node){
       Type lexpType = this.mCurrentST.getExpType(node.getLExp());
       Type rexpType = this.mCurrentST.getExpType(node.getRExp());
       if ((lexpType==Type.BYTE) &&
            (rexpType==Type.BYTE)
          ){
            this.mCurrentST.setExpType(node, Type.BYTE);

       }
       else{
           throw new SemanticException(
                "Operands to * operator must be BYTE",
           node.getLExp().getLine(),
           node.getLExp().getPos());
       }
   }
   @Override
   public void outEqualExp(EqualExp node){
       Type lexpType = this.mCurrentST.getExpType(node.getLExp());
       Type rexpType = this.mCurrentST.getExpType(node.getRExp());
       if ((lexpType==Type.INT || lexpType==Type.BYTE) &&
           (rexpType == Type.INT || rexpType==Type.BYTE) || lexpType == rexpType
          ){
                this.mCurrentST.setExpType(node, Type.BOOL);
       }
       else{
           throw new SemanticException(
                "Operands to == operator must be INT OR BYTE, or equal",
           node.getLExp().getLine(),
           node.getLExp().getPos());
       }
   }
   @Override
   public void outNegExp(NegExp node){
       Type rexpType = this.mCurrentST.getExpType(node.getExp());
       if (rexpType == Type.INT || rexpType==Type.BYTE){
            this.mCurrentST.setExpType(node, Type.INT);
       }
       else{
           throw new SemanticException(
                "Operands to - operator must be INT OR BYTE",
           node.getExp().getLine(),
           node.getExp().getPos());
       }
   }
   @Override
   public void outByteCast(ByteCast node){
       Type expType = this.mCurrentST.getExpType(node.getExp());
       if(expType==Type.INT || expType==Type.BYTE){
           this.mCurrentST.setExpType(node, Type.BYTE);
       }
       else{
           throw new SemanticException(
           "Operands to (Byte) cast must by INT or BYTE",
           node.getExp().getLine(),
           node.getExp().getPos());

       }
   }
   /*
   Other expressions
   ButtonExp
   ColorExp*/
//================================================================Other Expressions
    @Override
    public void visitIntLiteral(IntLiteral node){
        Type expType = this.mCurrentST.getExpType(node);
            this.mCurrentST.setExpType(node, Type.INT);
    }
    @Override
    public void visitButtonLiteral(ButtonLiteral node){
        Type expType = this.mCurrentST.getExpType(node);
            this.mCurrentST.setExpType(node, Type.BUTTON);

    }
    @Override
    public void visitColorLiteral(ColorLiteral node){
        Type expType = this.mCurrentST.getExpType(node);
            this.mCurrentST.setExpType(node, Type.COLOR);

    }
    @Override
    public void visitTrueLiteral(TrueLiteral node)
    {
        Type expType = this.mCurrentST.getExpType(node);
        this.mCurrentST.setExpType(node, Type.BOOL);
    }
    @Override
    public void visitFalseLiteral(FalseLiteral node)
    {
        Type expType = this.mCurrentST.getExpType(node);
        this.mCurrentST.setExpType(node, Type.BOOL);
    }
    /*==============================================================Statements
    BlockStatment
    IfStatement
    MeggyDelay
    MeggySetPixel
    WhileStatement*/

    @Override
    public void outIfStatement(IfStatement node)
    {
        Type expType = this.mCurrentST.getExpType(node.getExp());
            if(expType==Type.BOOL){
                this.mCurrentST.setExpType(node, Type.VOID);
            }
            else
            {
                throw new SemanticException(
                 "Param must be BOOL",
                node.getExp().getLine(),
                node.getExp().getPos());
        }
    }
    @Override
    public void outWhileStatement(WhileStatement node)
    {
        Type expType = this.mCurrentST.getExpType(node.getExp());
            if(expType==Type.BOOL){
                this.mCurrentST.setExpType(node, Type.BOOL);
            }
            else
            {
                throw new SemanticException(
                "Param must be BOOL",
                node.getExp().getLine(),
                node.getExp().getPos());
        }
    }
    @Override
    public void outMeggySetPixel(MeggySetPixel node)
    {
        Type xType = this.mCurrentST.getExpType(node.getXExp());
        Type yType = this.mCurrentST.getExpType(node.getYExp());
        Type zType = this.mCurrentST.getExpType(node.getColor());
        if(xType==Type.BYTE && yType==Type.BYTE&& zType ==Type.COLOR){
                this.mCurrentST.setExpType(node, Type.VOID);
        }
        else{
            throw new SemanticException(
                "values for setPixel not BYTE and COLOR",
                node.getXExp().getLine(),
                node.getXExp().getPos());

        }
    }
    @Override
    public void outMeggyDelay(MeggyDelay node)
    {
        Type expType = this.mCurrentST.getExpType(node.getExp());
        if(expType==Type.BYTE || expType==Type.INT){
            this.mCurrentST.setExpType(node, Type.VOID);
        }
        else{
            throw new SemanticException(
            "values for MeggyDelay must be BYTE or INT",
                node.getExp().getLine(),
                node.getExp().getPos());

        }
    }
    @Override
    public void outMeggyGetPixel(MeggyGetPixel node){
        Type xType = this.mCurrentST.getExpType(node.getXExp());
        Type yType = this.mCurrentST.getExpType(node.getYExp());
        if(xType==Type.BYTE && yType==Type.BYTE){
                this.mCurrentST.setExpType(node, Type.COLOR);
        }
        else{
            throw new SemanticException(
            "values for getPixel must be BYTE",
                node.getXExp().getLine(),
                node.getXExp().getPos());

        }
    }
    @Override
    public void outMeggyCheckButton(MeggyCheckButton node)
    {
        Type expType = this.mCurrentST.getExpType(node.getExp());
        if(expType==Type.BUTTON){
            this.mCurrentST.setExpType(node, Type.BOOL);
        }
        else{
            throw new SemanticException(
            "values for checkButton must be BUTTON",
                node.getExp().getLine(),
                node.getExp().getPos());

        }
    }
    public void outProgram(Program node)
    {
        this.mCurrentST.setExpType(node, Type.VOID);
    }
    public void outMainClass(MainClass node)
    {
        this.mCurrentST.setExpType(node, Type.VOID);
    }


}
