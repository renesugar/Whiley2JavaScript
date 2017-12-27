package wyjs.tasks;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import wycc.util.Pair;
import wyll.core.LowLevel;
import wyll.core.LowLevel.Type;
import wyll.core.LowLevel.Type.Array;
import wyll.core.LowLevel.Type.Bool;
import wyll.core.LowLevel.Type.Int;
import wyll.core.LowLevel.Type.Null;
import wyll.core.LowLevel.Type.Record;
import wyll.core.LowLevel.Type.Recursive;
import wyll.core.LowLevel.Type.Reference;
import wyll.core.LowLevel.Type.Union;
import wyll.core.LowLevel.Type.Void;
import wyll.util.LowLevelType;
import wyjs.core.JavaScriptFile;
import wyjs.core.JavaScriptFile.*;

public class JavaScriptCompiler implements LowLevel.Visitor<Declaration, Term, Term> {

	// =====================================================================
	// Declarations
	// =====================================================================

	@Override
	public Declaration visitStaticVariable(String name, Type type, Term initialiser) {
		if(initialiser != null) {
			return new VariableDeclaration(name,initialiser);
		} else {
			return new VariableDeclaration(name);
		}
	}

	@Override
	public Declaration visitType(String name, Type definition) {
		// Type aliases have no counterpart in JavaScript. We could potentially include
		// a comment perhaps.
		return null;
	}

	@Override
	public Declaration visitMethod(String name, List<Pair<Type, String>> parameters, Type retType, List<Term> body) {
		JavaScriptFile.Method method = new JavaScriptFile.Method(name);
		// Strip away type information
		List<String> methodParameters = method.getParameters();
		for(int i=0;i!=parameters.size();++i) {
			methodParameters.add(parameters.get(i).second());
		}
		// Set method body
		method.setBody(new JavaScriptFile.Block(body));
		//
		return method;
	}

	// =====================================================================
	// Statements
	// =====================================================================

	@Override
	public Term visitAssert(Term condition) {
		return new JavaScriptFile.Invoke(WY_RUNTIME, "assert", condition);
	}

	@Override
	public Term visitAssign(Term lhs, Term rhs) {
		return new JavaScriptFile.Assignment(lhs,rhs);
	}

	@Override
	public Term visitBreak() {
		return new JavaScriptFile.Break();
	}

	@Override
	public Term visitContinue() {
		return new JavaScriptFile.Continue();
	}

	@Override
	public Term visitDoWhile(Term condition, List<Term> body) {
		JavaScriptFile.Block block = new JavaScriptFile.Block(body);
		return new JavaScriptFile.DoWhile(block, condition);
	}

	@Override
	public Term visitFor(Term declaration, Term condition, Term increment, List<Term> body) {
		JavaScriptFile.Block block = new JavaScriptFile.Block(body);
		// FIXME: the need for this cast is something of a hack
		return new JavaScriptFile.For((VariableDeclaration) declaration, condition, increment, block);
	}

	@Override
	public Term visitIfElse(List<Pair<Term, List<Term>>> branches) {
		ArrayList<JavaScriptFile.IfElse.Case> cases = new ArrayList<>();
		for(int i=0;i!=branches.size();++i) {
			Pair<Term, List<Term>> cAse = branches.get(i);
			JavaScriptFile.Block block = new JavaScriptFile.Block(cAse.second());
			cases.add(new JavaScriptFile.IfElse.Case(cAse.first(),block));
		}
		return new JavaScriptFile.IfElse(cases);
	}

	@Override
	public Term visitReturn(Term rval) {
		return new JavaScriptFile.Return(rval);
	}

	@Override
	public Term visitSwitch(Term condition, List<Pair<Integer, List<Term>>> branches) {
		ArrayList<JavaScriptFile.Switch.Case> cases = new ArrayList<>();
		for (int i = 0; i != branches.size(); ++i) {
			Pair<Integer, List<Term>> cAse = branches.get(i);
			JavaScriptFile.Block block = new JavaScriptFile.Block(cAse.second());
			JavaScriptFile.Constant label = null;
			if (cAse.first() != null) {
				label = new JavaScriptFile.Constant(cAse.first());
			}
			cases.add(new JavaScriptFile.Switch.Case(label, block));
		}
		return new JavaScriptFile.Switch(condition, cases);
	}

	@Override
	public Term visitWhile(Term condition, List<Term> body) {
		JavaScriptFile.Block block = new JavaScriptFile.Block(body);
		return new JavaScriptFile.While(condition, block);
	}

	@Override
	public Term visitVariableDeclaration(Type type, String name, Term initialiser) {
		return new JavaScriptFile.VariableDeclaration(name,initialiser);
	}

	// =====================================================================
	// Expressions
	// =====================================================================

	@Override
	public Term visitVariableAccess(Type type, String name) {
		return new JavaScriptFile.VariableAccess(name);
	}

	@Override
	public Term visitStaticVariableAccess(Type type, String name) {
		return new JavaScriptFile.VariableAccess(name);
	}

	@Override
	public Term visitNullInitialiser() {
		return new JavaScriptFile.Constant(null);
	}

	@Override
	public Term visitDirectInvocation(Type.Method type, String name, List<Term> arguments) {
		return new JavaScriptFile.Invoke(null, name, arguments);
	}

	@Override
	public Term visitLambdaAccess(Type.Method type, String name) {
		// NOTE: the reason we use a function declaration here (i.e. instead of
		// just assigning the name) is that it protects against potential name
		// clashes with local variables.
		ArrayList<String> parameters = new ArrayList<>();
		for (int i = 0; i != type.numberOfParameters(); ++i) {
			parameters.add("p" + i);
		}
		ArrayList<Term> arguments = new ArrayList<>();
		for (int i = 0; i != type.numberOfParameters(); ++i) {
			arguments.add(new JavaScriptFile.VariableAccess("p" + i));
		}
		Term expr = new JavaScriptFile.Invoke(null, name, arguments);
		Block body = new JavaScriptFile.Block();
		body.getTerms().add(new JavaScriptFile.Return(expr));
		return new JavaScriptFile.Lambda(parameters, body);
	}

	@Override
	public Term visitIndirectInvocation(Type.Method type, Term target, List<Term> arguments) {
		return new JavaScriptFile.IndirectInvoke(target, arguments);
	}

	@Override
	public Term visitEqual(Type lhsT, Type rhsT, Term lhs, Term rhs) {
		if (lhsT instanceof Type.Primitive && rhsT instanceof Type.Primitive) {
			// FIXME: this will be a problem when dealing with unbound integer arithmetic
			return new JavaScriptFile.Operator(JavaScriptFile.Operator.Kind.EQ, lhs, rhs);
		} else {
			return new JavaScriptFile.Invoke(WY_RUNTIME, "equals", lhs, rhs);
		}
	}

	@Override
	public Term visitNotEqual(Type lhsT, Type rhsT, Term lhs, Term rhs) {
		if (lhsT instanceof Type.Primitive && rhsT instanceof Type.Primitive) {
			// FIXME: this will be a problem when dealing with unbound integer arithmetic
			return new JavaScriptFile.Operator(JavaScriptFile.Operator.Kind.NEQ, lhs, rhs);
		} else {
			Term eq = new JavaScriptFile.Invoke(WY_RUNTIME, "equals", lhs, rhs);
			return new JavaScriptFile.Operator(JavaScriptFile.Operator.Kind.NOT, eq);
		}
	}

	@Override
	public Term visitClone(Type type, Term expr) {
		if(type instanceof Type.Primitive) {
			return expr;
		} else {
			return new JavaScriptFile.Invoke(WY_RUNTIME, "copy", expr);
		}
	}

	@Override
	public Term visitLogicalInitialiser(boolean value) {
		return new JavaScriptFile.Constant(value);
	}

	@Override
	public Term visitLogicalAnd(Term lhs, Term rhs) {
		return new JavaScriptFile.Operator(JavaScriptFile.Operator.Kind.AND, lhs, rhs);
	}

	@Override
	public Term visitLogicalOr(Term lhs, Term rhs) {
		return new JavaScriptFile.Operator(JavaScriptFile.Operator.Kind.OR, lhs, rhs);
	}

	@Override
	public Term visitLogicalNot(Term expr) {
		return new JavaScriptFile.Operator(JavaScriptFile.Operator.Kind.NOT, expr);
	}

	@Override
	public Term visitLogicalEqual(Term lhs, Term rhs) {
		return new JavaScriptFile.Operator(JavaScriptFile.Operator.Kind.EQ, lhs, rhs);
	}

	@Override
	public Term visitLogicalNotEqual(Term lhs, Term rhs) {
		return new JavaScriptFile.Operator(JavaScriptFile.Operator.Kind.NEQ, lhs, rhs);
	}

	@Override
	public Term visitIntegerInitialiser(Int type, BigInteger value) {
		// FIXME: need to handle unbound arithmetic properly.
		return new Constant(value.intValue());
	}

	@Override
	public Term visitIntegerEqual(Int type, Term lhs, Term rhs) {
		return new JavaScriptFile.Operator(JavaScriptFile.Operator.Kind.EQ, lhs, rhs);
	}

	@Override
	public Term visitIntegerNotEqual(Int type, Term lhs, Term rhs) {
		return new JavaScriptFile.Operator(JavaScriptFile.Operator.Kind.NEQ, lhs, rhs);
	}

	@Override
	public Term visitIntegerLessThan(Int type, Term lhs, Term rhs) {
		return new JavaScriptFile.Operator(JavaScriptFile.Operator.Kind.LT, lhs, rhs);
	}

	@Override
	public Term visitIntegerLessThanOrEqual(Int type, Term lhs, Term rhs) {
		return new JavaScriptFile.Operator(JavaScriptFile.Operator.Kind.LTEQ, lhs, rhs);
	}

	@Override
	public Term visitIntegerGreaterThan(Int type, Term lhs, Term rhs) {
		return new JavaScriptFile.Operator(JavaScriptFile.Operator.Kind.GT, lhs, rhs);
	}

	@Override
	public Term visitIntegerGreaterThanOrEqual(Int type, Term lhs, Term rhs) {
		return new JavaScriptFile.Operator(JavaScriptFile.Operator.Kind.GTEQ, lhs, rhs);
	}

	@Override
	public Term visitIntegerAdd(Int type, Term lhs, Term rhs) {
		return new JavaScriptFile.Operator(JavaScriptFile.Operator.Kind.ADD, lhs, rhs);
	}

	@Override
	public Term visitIntegerSubtract(Int type, Term lhs, Term rhs) {
		return new JavaScriptFile.Operator(JavaScriptFile.Operator.Kind.SUB, lhs, rhs);
	}

	@Override
	public Term visitIntegerMultiply(Int type, Term lhs, Term rhs) {
		return new JavaScriptFile.Operator(JavaScriptFile.Operator.Kind.MUL, lhs, rhs);
	}

	@Override
	public Term visitIntegerDivide(Int type, Term lhs, Term rhs) {
		Term t = new JavaScriptFile.Operator(JavaScriptFile.Operator.Kind.DIV, lhs, rhs);
		return new JavaScriptFile.Invoke(new JavaScriptFile.VariableAccess("Math"), "floor", t);
	}

	@Override
	public Term visitIntegerRemainder(Int type, Term lhs, Term rhs) {
		return new JavaScriptFile.Operator(JavaScriptFile.Operator.Kind.REM, lhs, rhs);
	}

	@Override
	public Term visitIntegerNegate(Int type, Term expr) {
		return new JavaScriptFile.Operator(JavaScriptFile.Operator.Kind.NEG, expr);
	}

	@Override
	public Term visitIntegerCoercion(Int target, Int actual, Term expr) {
		return expr;
	}

	@Override
	public Term visitBitwiseEqual(Int target, Term lhs, Term rhs) {
		return new JavaScriptFile.Operator(JavaScriptFile.Operator.Kind.EQ, lhs, rhs);
	}

	@Override
	public Term visitBitwiseNotEqual(Int target, Term lhs, Term rhs) {
		return new JavaScriptFile.Operator(JavaScriptFile.Operator.Kind.NEQ, lhs, rhs);
	}

	@Override
	public Term visitBitwiseNot(Int type, Term expr) {
		Term lhs = new JavaScriptFile.Operator(JavaScriptFile.Operator.Kind.BITWISEINVERT, expr);
		Term rhs = new JavaScriptFile.Constant(0x0FF);
		return new JavaScriptFile.Operator(JavaScriptFile.Operator.Kind.BITWISEAND, lhs, rhs);
	}

	@Override
	public Term visitBitwiseAnd(Int type, Term lhs, Term rhs) {
		return new JavaScriptFile.Operator(JavaScriptFile.Operator.Kind.BITWISEAND, lhs, rhs);
	}

	@Override
	public Term visitBitwiseOr(Int type, Term lhs, Term rhs) {
		return new JavaScriptFile.Operator(JavaScriptFile.Operator.Kind.BITWISEOR, lhs, rhs);
	}

	@Override
	public Term visitBitwiseXor(Int type, Term lhs, Term rhs) {
		return new JavaScriptFile.Operator(JavaScriptFile.Operator.Kind.BITWISEXOR, lhs, rhs);
	}

	@Override
	public Term visitBitwiseShl(Int type, Term lhs, Term rhs) {
		Term l = new JavaScriptFile.Operator(JavaScriptFile.Operator.Kind.LEFTSHIFT, lhs, rhs);
		Term r = new JavaScriptFile.Constant(0x0FF);
		return new JavaScriptFile.Operator(JavaScriptFile.Operator.Kind.BITWISEAND, l, r);
	}

	@Override
	public Term visitBitwiseShr(Int type, Term lhs, Term rhs) {
		Term l = new JavaScriptFile.Operator(JavaScriptFile.Operator.Kind.RIGHTSHIFT, lhs, rhs);
		Term r = new JavaScriptFile.Constant(0x0FF);
		return new JavaScriptFile.Operator(JavaScriptFile.Operator.Kind.BITWISEAND, l, r);
	}

	@Override
	public Term visitArrayInitialiser(Array type, List<Term> operands) {
		return new JavaScriptFile.ArrayInitialiser(operands);
	}

	@Override
	public Term visitArrayInitialiser(Array type, Term length) {
		return new JavaScriptFile.ArrayInitialiser(new ArrayList<>());
	}

	@Override
	public Term visitArrayGenerator(Array type, Term value, Term length) {
		JavaScriptFile.Term runtime = new JavaScriptFile.VariableAccess("Wy");
		ArrayList<Term> arguments = new ArrayList<>();
		arguments.add(value);
		arguments.add(length);
		return new JavaScriptFile.Invoke(runtime, "array", arguments);
	}

	@Override
	public Term visitArrayLength(Array type, Term source) {
		return new JavaScriptFile.ArrayLength(source);
	}

	@Override
	public Term visitArrayAccess(List<Array> type, Term source, Term index) {
		if (type.size() > 1) {
			// indicates an effective array access, that is a union of array values.
			source = new JavaScriptFile.RecordAccess(source, "data");
		}
		return new JavaScriptFile.ArrayAccess(source, index);
	}

	@Override
	public Term visitRecordInitialiser(Record type, List<Term> operands) {
		// REQUIRES: |type.getFields()| == |operands|
		List<Pair<String, Term>> properties = new ArrayList<>();
		for (int i = 0; i != type.size(); ++i) {
			Pair<? extends Type, String> field = type.getField(i);
			properties.add(new Pair<>(field.second(), operands.get(i)));
		}
		return new JavaScriptFile.ObjectLiteral(properties);
	}

	@Override
	public Term visitRecordAccess(Record type, Term source, String field) {
		return new JavaScriptFile.PropertyAccess(source, field);
	}

	@Override
	public Term visitRecordCoercion(Int target, Int actual, Term expr) {
		throw new UnsupportedOperationException("implement me!");
	}

	@Override
	public Term visitReferenceInitialiser(Reference type, Term operand) {
		throw new UnsupportedOperationException("implement me!");
	}

	@Override
	public Term visitUnionEnter(Union type, int tag, Term expr) {
		ArrayList<Pair<String,Term>> fields = new ArrayList<>();
		fields.add(new Pair<>("tag",new JavaScriptFile.Constant(tag)));
		fields.add(new Pair<>("data",expr));
		return new JavaScriptFile.ObjectLiteral(fields);
	}

	@Override
	public Term visitUnionLeave(Union type, int tag, Term expr) {
		return new JavaScriptFile.RecordAccess(expr, "data");
	}

	@Override
	public Term visitUnionAccess(Union type, Term expr) {
		return new JavaScriptFile.RecordAccess(expr, "tag");
	}

	@Override
	public Term visitUnionCoercion(Union target, Union actual, Term expr) {
		throw new UnsupportedOperationException("implement me!");
	}

	@Override
	public Type.Int visitTypeInt(int width) {
		return JSInt;
	}

	@Override
	public Type.Void visitTypeVoid() {
		return JSVoid;
	}

	@Override
	public Type.Bool visitTypeBool() {
		return JSBool;
	}

	@Override
	public Type.Null visitTypeNull() {
		return JSNull;
	}

	@Override
	public Type.Array visitTypeArray(Type element) {
		return new LowLevelType.Array((LowLevelType) element);
	}

	@Override
	public Type.Reference visitTypeReference(Type element) {
		throw new UnsupportedOperationException("implement me!");
	}

	@Override
	public Type.Record visitTypeRecord(List<Pair<Type, String>> fields) {
		return new LowLevelType.Record((List) fields);
	}

	@Override
	public Type.Recursive visitTypeRecursive(String name) {
		return new LowLevelType.Recursive();
	}

	@Override
	public Type.Method visitTypeMethod(List<Type> parameters, Type returns) {
		return new LowLevelType.Method(parameters,returns);
	}

	@Override
	public Type.Union visitTypeUnion(List<Type> elements) {
		return new LowLevelType.Union(elements);
	}

	private static final Term WY_RUNTIME = new JavaScriptFile.VariableAccess("Wy");
	private static final LowLevelType.Void JSVoid = new LowLevelType.Void();
	private static final LowLevelType.Null JSNull = new LowLevelType.Null();
	private static final LowLevelType.Bool JSBool = new LowLevelType.Bool();
	private static final LowLevelType.Int JSInt = new LowLevelType.Int(0);

}
