// Copyright 2011 The Whiley Project Developers
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//    http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
package wyjs.io;

import java.io.*;
import java.util.*;

import wybs.lang.Build;
import wybs.lang.NameID;
import wybs.lang.NameResolver.ResolutionError;
import wyil.type.TypeSystem;

import static wyll.core.WyllFile.*;
import wyll.core.WyllFile;
import wyll.util.AbstractConsumer;


/**
* Writes WYIL bytecodes in a textual from to a given file.
*
* <b>NOTE:</b> currently, this class is somewhat broken since it does not
* provide any way to specify the output directory. Rather, it simply puts the
* WYIL file in the same place as the Whiley file.
*
* @author David J. Pearce
*
*/
public final class JavaScriptFileWriter extends AbstractConsumer<JavaScriptFileWriter.Context> {
	private final PrintWriter out;

	/**
	 * The type system is useful for managing nominal types and converting them
	 * into their underlying types.
	 */
	protected final TypeSystem typeSystem;
	// Debug options
	private boolean debug = true;

	public JavaScriptFileWriter(Build.Project project, TypeSystem typeSystem, PrintWriter writer) {
		this.typeSystem = typeSystem;
		this.out = writer;
	}

	public JavaScriptFileWriter(Build.Project project, TypeSystem typeSystem, OutputStream stream) {
		this.typeSystem = typeSystem;
		this.out = new PrintWriter(new OutputStreamWriter(stream));
	}

	// ======================================================================
	// Configuration Methods
	// ======================================================================

	public void setDebug(boolean flag) {
		this.debug = flag;
	}

	// ======================================================================
	// Apply Method
	// ======================================================================

	public void apply(WyllFile module) {
		this.visitWhileyFile(module, new Context(0));
		out.flush();
	}

	@Override
	public void visitStaticVariable(Decl.StaticVariable cd, Context context) {
		out.print("var " + cd.getName());
		if (cd.hasInitialiser()) {
			out.print(" = ");
			visitExpression(cd.getInitialiser(), context);
		}
		out.println(";");
	}

	@Override
	public void visitMethod(Decl.Method method, Context context) {
		//
		out.print("function ");
		out.print(method.getName());
		visitVariables(method.getParameters(), context);
		out.println(" {");
		if (method.getBody() != null) {
			visitBlock(method.getBody(), context);
		}
		out.println("}");
	}

	@Override
	public void visitVariables(Tuple<Decl.Variable> parameters, Context context) {
		out.print("(");
		for (int i = 0; i != parameters.size(); ++i) {
			if (i != 0) {
				out.print(", ");
			}
			Decl.Variable decl = parameters.get(i);
			writeType(decl.getType());
			out.print(decl.getName());
		}
		out.print(")");
	}

	@Override
	public void visitVariable(Decl.Variable decl, Context context) {
		tabIndent(context);
		out.print("var ");
		writeType(decl.getType());
		out.print(decl.getName());
		if (decl.hasInitialiser()) {
			out.print(" = ");
			visitExpression(decl.getInitialiser(), context);
			out.println(";");
		} else {
			out.println(";");
		}
	}

	@Override
	public void visitBlock(Stmt.Block stmt, Context context) {
		super.visitBlock(stmt, context.indent());
	}

	@Override
	public void visitStatement(Stmt stmt, Context context) {
		//
		switch(stmt.getOpcode()) {
		case EXPR_invoke:
		case EXPR_indirectinvoke:
			tabIndent(context);
			super.visitStatement(stmt, context);
			// Need as an invocation expression won't terminate itself.
			out.println(";");
			break;
		default:
			super.visitStatement(stmt, context);
		}
	}

	@Override
	public void visitAssert(Stmt.Assert c, Context context) {
		tabIndent(context);
		out.print("Wy.assert(");
		visitExpression(c.getCondition(), context);
		out.println(");");
	}

	@Override
	public void visitAssign(Stmt.Assign stmt, Context context) {
		tabIndent(context);
		//
		writeLVal(stmt.getLeftHandSide(), context);
		out.print(" = ");
		visitExpression(stmt.getRightHandSide(), context);
		out.println(";");
	}

	@Override
	public void visitBreak(Stmt.Break b, Context context) {
		tabIndent(context);
		out.println("break;");
	}

	@Override
	public void visitContinue(Stmt.Continue b, Context context) {
		tabIndent(context);
		out.println("continue;");
	}

	@Override
	public void visitDoWhile(Stmt.DoWhile b, Context context) {
		tabIndent(context);
		out.println("do {");
		visitBlock(b.getBody(), context);
		tabIndent(context.indent());
		out.print("} while(");
		visitExpression(b.getCondition(), context);
		out.println(");");
	}

	@Override public void visitFail(Stmt.Fail c, Context context) {
		tabIndent(context);
		out.println("Wy.assert(false);");
	}

	@Override
	public void visitForEach(Stmt.ForEach c, Context context) {
		Decl.Variable var = c.getVariable();
		tabIndent(context);
		out.print("for(var ");
		writeType(var.getType());
		out.print(var.getName());
		out.print(" = ");
		visitExpression(c.getStart(), context);
		out.print("; " + var.getName() + " < ");
		visitExpression(c.getEnd(), context);
		out.println("; " + var.getName() + "++) {");
		visitBlock(c.getBody(), context);
		tabIndent(context);
		out.println("}");
	}

	@Override
	public void visitIfElse(Stmt.IfElse b, Context context) {
		tabIndent(context);
		Tuple<Pair<Expr,Stmt.Block>> branches = b.getBranches();
		for (int i = 0; i != branches.size(); ++i) {
			Pair<Expr,Stmt.Block> branch = branches.get(i);
			if(i != 0) {
				out.print(" else ");
			}
			out.print("if(");
			visitExpression(branch.getFirst(), context);
			//
			out.println(") {");
			visitBlock(branch.getSecond(), context);
			tabIndent(context);
			out.print("}");
		}
		if(b.hasDefaultBranch()) {
			out.println(" else {");
			visitBlock(b.getDefaultBranch(), context);
			tabIndent(context);
			out.println("}");
		} else {
			out.println();
		}
	}

	@Override
	public void visitWhile(Stmt.While b, Context context) {
		tabIndent(context);
		out.print("while(");
		visitExpression(b.getCondition(), context);
		out.println(") {");
		visitBlock(b.getBody(), context);
		tabIndent(context);
		out.println("}");
	}

	@Override
	public void visitReturn(Stmt.Return stmt, Context context) {
		//
		tabIndent(context);
		out.print("return ");
		if (stmt.hasReturn()) {
			// easy case
			visitExpression(stmt.getReturn(), context);
		}
		out.println(";");
	}

	@Override
	public void visitSwitch(Stmt.Switch b, Context context) {
		out.print("switch(");
		visitExpression(b.getCondition(), context);
		out.println(") {");
		Tuple<Stmt.Case> cases = b.getCases();
		for (int i = 0; i != cases.size(); ++i) {
			// FIXME: ugly
			Stmt.Case cAse = cases.get(i);
			Tuple<Value.Int> values = cAse.getConditions();
			if (values.size() == 0) {
				tabIndent(context);
				out.println("default:");
			} else {
				for (int j = 0; j != values.size(); ++j) {
					tabIndent(context);
					out.print("case ");
					out.print(values.get(j));
					out.println(":");
				}
			}
			visitBlock(cAse.getBlock(), context);
			tabIndent(context);
			out.println("break;");
		}
		tabIndent(context);
		out.println("}");
	}

	/**
	 * Write a bracketed operand if necessary. Any operand whose human-readable
	 * representation can contain whitespace must have brackets around it.
	 *
	 * @param operand
	 * @param enclosing
	 * @param out
	 */
	private void writeBracketedExpression(Expr expr, Context context) {
		boolean needsBrackets = needsBrackets(expr);
		if (needsBrackets) {
			out.print("(");
		}
		visitExpression(expr, context);
		if (needsBrackets) {
			out.print(")");
		}
	}

	@Override
	public void visitClone(Expr.Clone expr, Context context) {
		if(hasCopySemantics(expr.getType())) {
			visitExpression(expr.getOperand(), context);
		} else {
			out.print("Wy.copy(");
			visitExpression(expr.getOperand(), context);
			out.print(")");
		}
	}

	@Override
	public void visitConstant(Expr.Constant expr, Context context) {
		Value val = expr.getValue();
		if (val instanceof Value.Byte) {
			Value.Byte b = (Value.Byte) val;
			// FIXME: support es6 binary literals
			// out.print("0b");
			out.print("parseInt('");
			out.print(Integer.toBinaryString(b.get() & 0xFF));
			out.print("',2)");
		} else if (val instanceof Value.UTF8) {
			Value.UTF8 s = (Value.UTF8) val;
			byte[] bytes = s.get();
			out.print("[");
			for (int i = 0; i != bytes.length; ++i) {
				if (i != 0) {
					out.print(", ");
				}
				out.print(bytes[i]);
			}
			out.print("]");
		} else {
			out.print(val);
		}
	}

	@Override
	public void visitEqual(Expr.Equal expr, Context context) {
		visitEqualityOperator(expr, context);
	}

	@Override
	public void visitNotEqual(Expr.NotEqual expr, Context context) {
		visitEqualityOperator(expr, context);
	}

	private void visitEqualityOperator(Expr.BinaryOperator expr, Context context) {
		// Extract the type information
		Expr lhs = expr.getFirstOperand();
		Expr rhs = expr.getSecondOperand();
//		Type lhsT = lhs.getType();
//		Type rhsT = rhs.getType();
		// FIXME: what to do here ???
//		if (isCopyable(lhsT, lhs) && isCopyable(rhsT, rhs)) {
//			writeInfixOperator(expr, context);
//		} else {
			if (expr instanceof Expr.NotEqual) {
				out.print("!");
			}
			out.print("Wy.equals(");
			visitExpression(lhs, context);
			out.print(", ");
			visitExpression(rhs, context);
			out.print(")");
//		}
	}

	@Override
	public void visitStaticVariableAccess(Expr.StaticVariableAccess expr, Context context) {
		out.print(expr.getName());
	}

	@Override
	public void visitUnionAccess(Expr.UnionAccess expr, Context context) {
		visitExpression(expr.getOperand(), context);
		out.print(".tag");
	}

	@Override
	public void visitUnionLeave(Expr.UnionLeave expr, Context context) {
		visitExpression(expr.getOperand(), context);
		out.print(".data");
	}

	@Override
	public void visitUnionEnter(Expr.UnionEnter expr, Context context) {
		out.print("{tag: " + expr.getTag() + ", data: ");
		visitExpression(expr.getOperand(), context);
		out.print("}");
	}

	@Override
	public void visitVariableAccess(Expr.VariableAccess expr, Context context) {
		out.print(expr.getName());
	}

	// ================================================================================
	// Arrays
	// ================================================================================

	@Override
	public void visitArrayLength(Expr.ArrayLength expr, Context context) {
		visitExpression(expr.getOperand(), context);
		out.print(".length");
	}

	@Override
	public void visitArrayAccess(Expr.ArrayAccess expr, Context context) {
		System.out.println("TYPE: " + expr.getType() + " FROM: " + expr.getClass().getName());
		// FIXME: need to clone here
		visitExpression(expr.getFirstOperand(), context);
		out.print("[");
		visitExpression(expr.getSecondOperand(), context);
		out.print("]");
	}

	@Override
	public void visitArrayInitialiser(Expr.ArrayInitialiser expr, Context context) {
		Tuple<Expr> operands = expr.getOperands();
		out.print("[");
		for (int i = 0; i != operands.size(); ++i) {
			if (i != 0) {
				out.print(", ");
			}
			visitExpression(operands.get(i), context);
		}
		out.print("]");
	}

	@Override
	public void visitArrayGenerator(Expr.ArrayGenerator expr, Context context) {
		out.print("Wy.array(");
		visitExpression(expr.getFirstOperand(), context);
		out.print(", ");
		visitExpression(expr.getSecondOperand(), context);
		out.print(")");
	}

	// ================================================================================
	// Bitwise
	// ================================================================================

	@Override
	public void visitBitwiseAnd(Expr.BitwiseAnd expr, Context context) {
		writeInfixOperator(expr,context);
	}

	@Override
	public void visitBitwiseComplement(Expr.BitwiseComplement expr, Context context) {
		// Prefix operators
		out.print("((~");
		writeBracketedExpression(expr.getOperand(), context);
		out.print(") & 0xFF)");
	}

	@Override
	public void visitBitwiseOr(Expr.BitwiseOr expr, Context context) {
		writeInfixOperator(expr,context);
	}

	@Override
	public void visitBitwiseShiftLeft(Expr.BitwiseShiftLeft expr, Context context) {
		out.print("((");
		writeBracketedExpression(expr.getFirstOperand(), context);
		out.print(" << ");
		writeBracketedExpression(expr.getSecondOperand(), context);
		out.print(") & 0xFF)");
	}

	@Override
	public void visitBitwiseShiftRight(Expr.BitwiseShiftRight expr, Context context) {
		out.print("((");
		writeBracketedExpression(expr.getFirstOperand(), context);
		out.print(" >> ");
		writeBracketedExpression(expr.getSecondOperand(), context);
		out.print(") & 0xFF)");
	}

	@Override
	public void visitBitwiseXor(Expr.BitwiseXor expr, Context context) {
		writeInfixOperator(expr,context);
	}

	// ================================================================================
	// Callables
	// ================================================================================

	@Override
	public void visitIndirectInvoke(Expr.IndirectInvoke expr, Context context) {
		visitExpression(expr.getSource(), context);
		Tuple<Expr> arguments = expr.getArguments();
		out.print("(");
		for (int i = 0; i != arguments.size(); ++i) {
			if (i != 0) {
				out.print(", ");
			}
			visitExpression(arguments.get(i), context);
		}
		out.print(")");
	}

	@Override
	public void visitInvoke(Expr.Invoke expr, Context context) {
		Name name = expr.getName();
		// FIXME: this doesn't work for imported function symbols!
		out.print(name.getLast());
		out.print("(");
		Tuple<Expr> args = expr.getOperands();
		for (int i = 0; i != args.size(); ++i) {
			if (i != 0) {
				out.print(", ");
			}
			visitExpression(args.get(i), context);
		}
		out.print(")");
	}

	@Override
	public void visitLambda(Expr.Lambda expr, Context context) {
		out.print("function(");
		Tuple<Decl.Variable> parameters = expr.getParameters();
		for (int i = 0; i != parameters.size(); ++i) {
			Decl.Variable var = parameters.get(i);
			if (i != 0) {
				out.print(", ");
			}
			writeType(var.getType());
			out.print(var.getName());
		}
		out.print(") { ");
		out.print("return ");
		visitExpression(expr.getBody(), context);
		out.print("; }");
	}

	@Override
	public void visitLambdaAccess(Expr.LambdaAccess expr, Context context) {
		// NOTE: the reason we use a function declaration here (i.e. instead of
		// just assigning the name) is that it protects against potential name
		// clashes with local variables.
		Type.Method ft = expr.getSignature();
		Tuple<Type> params = ft.getParameters();
		out.print("function(");
		for (int i = 0; i != params.size(); ++i) {
			if (i != 0) {
				out.print(",");
			}
			out.print("p" + i);
		}
		out.print(") { return ");
		out.print(expr.getName());
		out.print("(");
		for (int i = 0; i != params.size(); ++i) {
			if (i != 0) {
				out.print(",");
			}
			out.print("p" + i);
		}
		out.print("); }");
	}

	// ================================================================================
	// Integers
	// ================================================================================

	@Override
	public void visitIntegerAddition(Expr.IntegerAddition expr, Context context) {
		writeInfixOperator(expr,context);
	}

	@Override
	public void visitIntegerDivision(Expr.IntegerDivision expr, Context context) {
		out.print("Math.floor(");
		writeInfixOperator(expr, context);
		out.print(")");
	}

	@Override
	public void visitIntegerRemainder(Expr.IntegerRemainder expr, Context context) {
		writeInfixOperator(expr, context);
	}

	@Override
	public void visitIntegerGreaterThan(Expr.IntegerGreaterThan expr, Context context) {
		writeInfixOperator(expr,context);
	}

	@Override
	public void visitIntegerGreaterThanOrEqual(Expr.IntegerGreaterThanOrEqual expr, Context context) {
		writeInfixOperator(expr,context);
	}

	@Override
	public void visitIntegerLessThan(Expr.IntegerLessThan expr, Context context) {
		writeInfixOperator(expr,context);
	}

	@Override
	public void visitIntegerLessThanOrEqual(Expr.IntegerLessThanOrEqual expr, Context context) {
		writeInfixOperator(expr,context);
	}

	@Override
	public void visitIntegerMultiplication(Expr.IntegerMultiplication expr, Context context) {
		writeInfixOperator(expr,context);
	}

	@Override
	public void visitIntegerNegation(Expr.IntegerNegation expr, Context context) {
		// Prefix operators
		out.print("-");
		writeBracketedExpression(expr.getOperand(), context);
	}

	@Override
	public void visitIntegerSubtraction(Expr.IntegerSubtraction expr, Context context) {
		writeInfixOperator(expr,context);
	}

	// ================================================================================
	// Logical
	// ================================================================================

	@Override
	public void visitLogicalAnd(Expr.LogicalAnd expr, Context context) {
		writeInfixOperator(expr,context);
	}

	@Override
	public void visitLogicalNot(Expr.LogicalNot expr, Context context) {
		// Prefix operators
		out.print("!");
		writeBracketedExpression(expr.getOperand(), context);
	}

	@Override
	public void visitLogicalOr(Expr.LogicalOr expr, Context context) {
		writeInfixOperator(expr,context);
	}

	// ================================================================================
	// Objects
	// ================================================================================

	@Override
	public void visitNew(Expr.New expr, Context context) {
		out.print("new Wy.Ref(");
		visitExpression(expr.getOperand(), context);
		out.print(")");
	}

	@Override
	public void visitDereference(Expr.Dereference expr, Context context) {
		// FIXME: need to clone here
		out.print("Wy.deref(");
		visitExpression(expr.getOperand(), context);
		out.print(")");
	}

	// ================================================================================
	// Records
	// ================================================================================

	@Override
	public void visitRecordAccess(Expr.RecordAccess expr, Context context) {
		// FIXME: need to clone here
		writeBracketedExpression(expr.getOperand(), context);
		out.print("." + expr.getField());
	}

	@Override
	public void visitRecordInitialiser(Expr.RecordInitialiser expr, Context context) {
		out.print("Wy.record({");
		Tuple<Expr> operands = expr.getOperands();
		Tuple<Identifier> fields = expr.getFields();
		for (int i = 0; i != operands.size(); ++i) {
			if (i != 0) {
				out.print(", ");
			}
			out.print(fields.get(i));
			out.print(": ");
			visitExpression(operands.get(i), context);
		}
		out.print("})");
	}

	// ================================================================================
	// Helpers
	// ================================================================================

	private void writeName(Name name) {
		for(int i=0;i!=name.size();++i) {
			if (i != 0) {
				// FIXME: this is a temporary hack for now.
				out.print("$");
			}
			out.print(name.get(i).get());
		}
	}

	private void writeShadowVariables(Tuple<Decl.Variable> parameters, boolean restore, Context context) {
		if (debug) {
			tabIndent(context);
			if(restore) {
				out.println("// restore shadow variables");
			} else {
				out.println("// create shadow variables");
			}
			tabIndent(context);
			for (int i = 0; i != parameters.size(); ++i) {
				if (i != 0) {
					out.print(" ");
				}
				String var = parameters.get(i).getName().get();
				if (restore) {
					out.print(var + " = $" + var);
				} else {
					out.print("var $" + var + " = " + var);
				}
				out.print(";");
			}
			out.println();
		}
	}

	private void writeInvariantTest(Decl.Variable var, Context context) {
		String name = var.getName().get();
		writeInvariantTest(name, 0, var.getType(), context);
	}

	private void writeInvariantTest(String access, int depth, Type type, Context context) {
		switch(type.getOpcode()) {
		case TYPE_record:
			writeInvariantTest(access, depth, (Type.Record) type, context);
			break;
		case TYPE_array:
			writeInvariantTest(access, depth, (Type.Array) type, context);
			 break;
		case TYPE_reference:
			writeInvariantTest(access, depth, (Type.Reference) type, context);
			break;
//		case TYPE_negation:
//			writeInvariantTest(access, depth, (Type.Negation) type, context);
//			break;
		case TYPE_union:
			writeInvariantTest(access, depth, (Type.Union) type, context);
			break;
		default:
			// Do nothing
		}
	}

	private void writeInvariantTest(String access, int depth, Type.Record type, Context context) {
		Tuple<Decl.Variable> fields = type.getFields();
		for (int i = 0; i != fields.size(); ++i) {
			Decl.Variable field = fields.get(i);
			writeInvariantTest(access + "." + field.getName().get(), depth, field.getType(), context);
		}
	}

	private static String[] indexVariableNames = { "i", "j", "k", "l", "m" };

	private void writeInvariantTest(String access, int depth, Type.Array type, Context context) {
		int variableNameIndex = depth % indexVariableNames.length;
		int variableNameGroup = depth / indexVariableNames.length;
		String var = indexVariableNames[variableNameIndex];
		if(variableNameGroup > 0) {
			// In case we wrap around the selection of variable names.
			var = var + variableNameGroup;
		}
		tabIndent(context);
		out.println("for(var " + var + "=0; " + var + "<" + access + ".length; " + var + "++) {");
		writeInvariantTest(access + "[" + var + "]", depth + 1, type.getElement(), context.indent());
		tabIndent(context);
		out.println("}");
	}

	private void writeInvariantTest(String access, int depth, Type.Reference type, Context context) {
		// FIXME: to do.
	}

//	private void writeInvariantTest(String access, int depth, Type.Negation type, Context context) {
//		out.println("if(" + getTypeTest(type.getElement(),access,context) + ") { return false; }");
//	}

	private void writeInvariantTest(String access, int depth, Type.Union type, Context context) {
		for (int i = 0; i != type.size(); ++i) {
			Type bound = type.get(i);
			tabIndent(context);
			if(i != 0) {
				out.print("else ");
			}
			// FIXME: this could be made more efficient
			out.println("if(" + getTypeTest(bound,access,context) + ") {}");
		}
		tabIndent(context);
		out.println("else { return false; }");
	}

	private String getTypeTest(Type t, String access, Context context) {
		// Handle all non-trivial cases directly
		if (t instanceof Type.Null) {
			return access + " === null";
		} else if (t instanceof Type.Int) {
			return "typeof " + access + " === \"number\"";
		} else if (t instanceof Type.Bool) {
			return "typeof " + access + " === \"boolean\"";
		} else {
			// Fall back case
			String r = "is$" + getTypeMangle(t) + "(" + access + ")";
			// Register this type test to be written out as an appropriately
			// named function.
			return r;
		}
	}


	private void writeInvariantCheck(LVal lval, Context context) {
		switch (lval.getOpcode()) {
		case EXPR_arrayaccess: {
			Expr.ArrayAccess e = (Expr.ArrayAccess) lval;
			writeInvariantCheck((LVal) e.getFirstOperand(), context);
			break;
		}
		case EXPR_dereference: {
			Expr.Dereference e = (Expr.Dereference) lval;
			writeInvariantCheck((LVal) e.getOperand(), context);
			break;
		}
		case EXPR_recordaccess:{
			Expr.RecordAccess e = (Expr.RecordAccess) lval;
			writeInvariantCheck((LVal) e.getOperand(), context);
			break;
		}
		default:
			throw new IllegalArgumentException("invalid lval: " + lval);
		}
	}

	private void writeInvariantCheck(Decl.Variable var, Context context) {
		if (debug) {
			// FIXME: This is completely broken. For example, consider the type "nat[]" ...
			// it gets completely ignored here.
//			Type type = var.getType();
//			if (type instanceof Type.Nominal) {
//				Type.Nominal nom = (Type.Nominal) type;
//				tabIndent(context);
//				out.println("// check type invariant");
//				tabIndent(context);
//				out.print("Wy.assert(");
//				writeName(nom.getName());
//				out.println("$type(" + var.getName().get() + "));");
//			}
		}
	}

	private void writeInvariantCheck(Tuple<Expr> invariant, String comment, Context context) {
		if(debug && invariant.size() > 0) {
			tabIndent(context);
			out.println("// check " + comment);
			for(int i=0;i!=invariant.size();++i) {
				tabIndent(context);
				out.print("Wy.assert(");
				visitExpression(invariant.get(i), context);
				out.println(");");
			}
		}
	}

	private void writeInfixOperator(Expr.BinaryOperator expr, Context context) {
		writeBracketedExpression(expr.getFirstOperand(), context);
		out.print(" ");
		out.print(opcode(expr.getOpcode()));
		out.print(" ");
		writeBracketedExpression(expr.getSecondOperand(), context);
	}

	private void writeInfixOperator(Expr.NaryOperator expr, Context context) {
		Tuple<Expr> operands = expr.getOperands();
		for (int i = 0; i != operands.size(); ++i) {
			if (i != 0) {
				out.print(" ");
				out.print(opcode(expr.getOpcode()));
				out.print(" ");
			}
			writeBracketedExpression(operands.get(i), context);
		}
	}

	private void writeLVal(LVal lval, Context context) {
		switch (lval.getOpcode()) {
		case EXPR_arrayaccess:
			writeArrayIndexLVal((Expr.ArrayAccess) lval, context);
			break;
		case EXPR_dereference:
			writeDereferenceLVal((Expr.Dereference) lval, context);
			break;
		case EXPR_recordaccess:
			writeFieldLoadLVal((Expr.RecordAccess) lval, context);
			break;
		case EXPR_variableaccess:
			writeVariableAccessLVal((Expr.VariableAccess) lval, context);
			break;
		default:
			throw new IllegalArgumentException("invalid lval: " + lval);
		}
	}

	private void writeDereferenceLVal(Expr.Dereference expr, Context context) {
		writeLVal((LVal) expr.getOperand(), context);
		out.print(".$ref");
	}

	private void writeArrayIndexLVal(Expr.ArrayAccess expr, Context context) {
		writeLVal((LVal) expr.getFirstOperand(), context);
		out.print("[");
		visitExpression(expr.getSecondOperand(), context);
		out.print("]");
	}

	private void writeFieldLoadLVal(Expr.RecordAccess expr, Context context) {
		writeLVal((LVal) expr.getOperand(), context);
		out.print("." + expr.getField());
	}

	private void writeVariableAccessLVal(Expr.VariableAccess expr, Context context) {
		//out.print(vd.getName());
	}

	private void writeTypeTests(Set<Type> typeTests, Set<Type> allTests) {
		HashSet<Type> deps = new HashSet<>();
		for(Type type : typeTests) {
			out.print("function is$");
			writeTypeMangle(type);
			out.print("(val) {");
			writeTypeTest(type, deps);
			out.println("}");
			out.println();
		}
		deps.removeAll(allTests);
		allTests.addAll(deps);
		if(deps.size() > 0) {
			writeTypeTests(deps,allTests);
		}
	}

	private void writeTypeTest(Type test, Set<Type> deps) {
		if(test instanceof Type.Null) {
			writeTypeTestNull((Type.Primitive) test,deps);
		} else if(test instanceof Type.Bool) {
			writeTypeTestBool((Type.Primitive) test,deps);
		} else if(test instanceof Type.Int) {
			writeTypeTestInt((Type.Primitive) test,deps);
		} else if(test instanceof Type.Array) {
			writeTypeTestArray((Type.Array) test,deps);
		} else if(test instanceof Type.Reference) {
			writeTypeTestReference((Type.Reference) test,deps);
		} else if(test instanceof Type.Record) {
			writeTypeTestRecord((Type.Record) test,deps);
		} else if(test instanceof Type.Union) {
			writeTypeTestUnion((Type.Union) test,deps);
		} else {
			throw new RuntimeException("unknown type encountered: " + test);
		}
	}

	private void writeTypeTestNull(Type.Primitive test, Set<Type> deps) {
		out.print(" return val === null; ");
	}

	private void writeTypeTestBool(Type.Primitive test, Set<Type> deps) {
		out.print(" return typeof val === \"boolean\"; ");
	}

	private void writeTypeTestInt(Type.Primitive test, Set<Type> deps) {
		out.print(" return typeof val === \"number\"; ");
	}

	private static int variableIndex = 0;

	private void writeTypeTestArray(Type.Array test, Set<Type> deps) {
		out.println();
		tabIndent(1);
		out.println("if(val != null && val.constructor === Array) {");
		tabIndent(2);
		// FIXME: could optimise this in the case of element "any"
		String var = "i" + (variableIndex++);
		out.println("for(var x=0;x!=val.length;++x) {".replaceAll("x", var));
		tabIndent(3);
		out.print("if(!is$");
		writeTypeMangle(test.getElement());
		out.println("(val[" + var +"])) {");
		tabIndent(4);
		out.println("return false;");
		tabIndent(3);
		out.println("}");
		tabIndent(2);
		out.println("}");
		tabIndent(2);
		out.println("return true;");
		tabIndent(1);
		out.println("}");
		tabIndent(1);
		out.println("return false;");
		// Add a follow-on dependency
		deps.add(test.getElement());
	}

	private void writeTypeTestReference(Type.Reference test, Set<Type> deps) {
		out.println();
		tabIndent(1);
		out.println("if(val != null && val.constructor === Wy.Ref) {");
		tabIndent(2);
		out.print(" return is$");
		writeTypeMangle(test.getElement());
		out.println("(Wy.deref(val));");
		tabIndent(1);
		out.println("}");
		tabIndent(1);
		out.println("return false;");
		//
		deps.add(test.getElement());
	}

	private void writeTypeTestRecord(Type.Record test, Set<Type> deps) {
		out.println();
		tabIndent(1);
		out.print("if(val != null && typeof val === \"object\"");
		Tuple<Decl.Variable> fields = test.getFields();
		if (!test.isOpen()) {
			out.print(" && Object.keys(val).length === " + fields.size());
		}
		out.println(") {");
		for (int i = 0; i != fields.size(); ++i) {
			Decl.Variable field = fields.get(i);
			tabIndent(2);
			out.print("if(val." + field.getName() + " === \"undefined\" || !is$");
			writeTypeMangle(field.getType());
			out.println("(val." + field.getName() + ")) { return false; }");
			deps.add(field.getType());
		}
		tabIndent(2);
		out.println("return true;");
		tabIndent(1);
		out.println("}");
		tabIndent(1);
		out.println("return false;");
	}

	private void writeTypeTestUnion(Type.Union test, Set<Type> deps) {
		out.println();
		for(int i=0;i!=test.size();++i) {
			Type bound = test.get(i);
			tabIndent(1);
			out.print("if(is$");
			writeTypeMangle(bound);
			out.println("(val)) { return true; }");
			//
			deps.add(bound);
		}
		tabIndent(1);
		out.print("return false;");
	}

	private void writeTypeMangle(Type t) {
		out.print(getTypeMangle(t));
	}

	private String getTypeMangle(Type t) {
		if (t instanceof Type.Null) {
			return "N";
		} else if (t instanceof Type.Bool) {
			return "B";
		} else if (t instanceof Type.Int) {
			return "I";
		} else if (t instanceof Type.Array) {
			return getTypeMangleArray((Type.Array) t);
		} else if (t instanceof Type.Reference) {
			return getTypeMangleReference((Type.Reference) t);
		} else if (t instanceof Type.Record) {
			return getTypeMangleRecord((Type.Record) t);
		} else if (t instanceof Type.Union) {
			return getTypeMangleUnion((Type.Union) t);
		} else {
			throw new IllegalArgumentException("unknown type encountered: " + t);
		}
	}

	private String getTypeMangleArray(Type.Array t) {
		return "a" + getTypeMangle(t.getElement());
	}
	private String getTypeMangleReference(Type.Reference t) {
		String r = "p";
		return r + getTypeMangle(t.getElement());
	}

	private String getTypeMangleRecord(Type.Record rt) {
		String r = "r";
		Tuple<Decl.Variable> fields = rt.getFields();
		r += fields.size();
		for (int i = 0; i != fields.size(); ++i) {
			Decl.Variable field = fields.get(i);
			r += getTypeMangle(field.getType());
			String fieldName = field.getName().get();
			r += fieldName.length();
			r += fieldName;
		}
		return r;
	}

	private String getTypeMangleUnion(Type.Union t) {
		String r = "u";
		r += t.size();
		for(int i=0;i!=t.size();++i) {
			r += getTypeMangle(t.get(i));
		}
		return r;
	}

	private void writeType(Type t) {
		if(debug) {
			out.print("/*");
			out.print(t);
			out.print("*/ ");
		}
	}

	private boolean hasCopySemantics(Type t) {
		switch(t.getOpcode()) {
		case TYPE_bool:
		case TYPE_null:
		case TYPE_int:
			return true;
		default:
			return false;
		}
	}

	private boolean needsBrackets(Expr e) {
		switch(e.getOpcode()) {
		case EXPR_integeraddition:
		case EXPR_integersubtraction:
		case EXPR_integermultiplication:
		case EXPR_integerdivision:
		case EXPR_integerremainder:
		case EXPR_equal:
		case EXPR_notequal:
		case EXPR_integerlessthan:
		case EXPR_integerlessequal:
		case EXPR_integergreaterthan:
		case EXPR_integergreaterequal:
		case EXPR_logicaland:
		case EXPR_logicalor:
		case EXPR_bitwiseor:
		case EXPR_bitwisexor:
		case EXPR_bitwiseand:
		case EXPR_bitwiseshl:
		case EXPR_bitwiseshr:
		case EXPR_new:
		case EXPR_unionenter:
		case EXPR_unionleave:
			return true;
		}
		return false;
	}

	private static String opcode(int k) {
		switch(k) {
		case EXPR_integernegation:
			return "-";
		case EXPR_logicalnot:
			return "!";
		case EXPR_bitwisenot:
			return "~";
		case EXPR_dereference:
			return "*";
		// Binary
		case EXPR_integeraddition:
			return "+";
		case EXPR_integersubtraction:
			return "-";
		case EXPR_integermultiplication:
			return "*";
		case EXPR_integerdivision:
			return "/";
		case EXPR_integerremainder:
			return "%";
		case EXPR_equal:
			return "==";
		case EXPR_notequal:
			return "!=";
		case EXPR_integerlessthan:
			return "<";
		case EXPR_integerlessequal:
			return "<=";
		case EXPR_integergreaterthan:
			return ">";
		case EXPR_integergreaterequal:
			return ">=";
		case EXPR_logicaland:
			return "&&";
		case EXPR_logicalor:
			return "||";
		case EXPR_bitwiseor:
			return "|";
		case EXPR_bitwisexor:
			return "^";
		case EXPR_bitwiseand:
			return "&";
		case EXPR_bitwiseshl:
			return "<<";
		case EXPR_bitwiseshr:
			return ">>";
		case EXPR_new:
			return "new";
		default:
			throw new IllegalArgumentException("unknown operator kind : " + k);
		}
	}

	private void tabIndent(Context context) {
		tabIndent(context.indent);
	}

	private void tabIndent(int indent) {
		indent = indent * 4;
		for (int i = 0; i < indent; ++i) {
			out.print(" ");
		}
	}

	public static class Context {
		public final int indent;

		public Context(int indent) {
			this.indent = indent;
		}

		public Context indent() {
			return new Context(indent + 1);
		}
	}
}