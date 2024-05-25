/*
 * Copyright (c) 2017 SpaceToad and the BuildCraft team
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0. If a copy of the MPL was not
 * distributed with this file, You can obtain one at https://mozilla.org/MPL/2.0/
 */

package buildcraft.lib.expression.node.func;

import java.util.Objects;

import buildcraft.lib.expression.NodeInliningHelper;
import buildcraft.lib.expression.api.IDependantNode;
import buildcraft.lib.expression.api.IDependancyVisitor;
import buildcraft.lib.expression.api.IExpressionNode.INodeBoolean;
import buildcraft.lib.expression.api.IExpressionNode.INodeDouble;
import buildcraft.lib.expression.api.IExpressionNode.INodeLong;
import buildcraft.lib.expression.api.IExpressionNode.INodeObject;
import buildcraft.lib.expression.api.INodeFunc.INodeFuncDouble;
import buildcraft.lib.expression.api.INodeStack;
import buildcraft.lib.expression.api.InvalidExpressionException;
import buildcraft.lib.expression.api.NodeTypes;
import buildcraft.lib.expression.node.func.StringFunctionTri;
import buildcraft.lib.expression.node.func.NodeFuncBase;
import buildcraft.lib.expression.node.func.NodeFuncBase.IFunctionNode;
import buildcraft.lib.expression.node.value.NodeConstantDouble;

// AUTO_GENERATED FILE, DO NOT EDIT MANUALLY!
public class NodeFuncDoubleDoubleToDouble extends NodeFuncBase implements INodeFuncDouble {

    public final IFuncDoubleDoubleToDouble function;
    private final StringFunctionTri stringFunction;

    public NodeFuncDoubleDoubleToDouble(String name, IFuncDoubleDoubleToDouble function) {
        this(function, (a, b) -> "[ double, double -> double ] " + name + "(" + a + ", " + b +  ")");
    }

    public NodeFuncDoubleDoubleToDouble(IFuncDoubleDoubleToDouble function, StringFunctionTri stringFunction) {

        this.function = function;
        this.stringFunction = stringFunction;
    }

    @Override
    public String toString() {
        return stringFunction.apply("{A}", "{B}");
    }

    @Override
    public NodeFuncDoubleDoubleToDouble setNeverInline() {
        super.setNeverInline();
        return this;
    }

    @Override
    public INodeDouble getNode(INodeStack stack) throws InvalidExpressionException {

        INodeDouble b = stack.popDouble();
        INodeDouble a = stack.popDouble();

        return create(a, b);
    }

    /** Shortcut to create a new {@link FuncDoubleDoubleToDouble} without needing to create
     *  and populate an {@link INodeStack} to pass to {@link #getNode(INodeStack)}. */
    public FuncDoubleDoubleToDouble create(INodeDouble argA, INodeDouble argB) {
        return new FuncDoubleDoubleToDouble(argA, argB); 
    }

    public class FuncDoubleDoubleToDouble implements INodeDouble, IDependantNode, IFunctionNode {
        public final INodeDouble argA;
        public final INodeDouble argB;

        public FuncDoubleDoubleToDouble(INodeDouble argA, INodeDouble argB) {
            this.argA = argA;
            this.argB = argB;

        }

        @Override
        public double evaluate() {
            return function.apply(argA.evaluate(), argB.evaluate());
        }

        @Override
        public INodeDouble inline() {
            if (!canInline) {
                // Note that we can still inline the arguments, just not *this* function
                return NodeInliningHelper.tryInline(this, argA, argB,
                    (a, b) -> new FuncDoubleDoubleToDouble(a, b),
                    (a, b) -> new FuncDoubleDoubleToDouble(a, b)
                );
            }
            return NodeInliningHelper.tryInline(this, argA, argB,
                (a, b) -> new FuncDoubleDoubleToDouble(a, b),
                (a, b) -> NodeConstantDouble.of(function.apply(a.evaluate(), b.evaluate()))
            );
        }

        @Override
        public void visitDependants(IDependancyVisitor visitor) {
            if (!canInline) {
                if (function instanceof IDependantNode) {
                    visitor.dependOn((IDependantNode) function);
                } else {
                    visitor.dependOnExplictly(this);
                }
            }
            visitor.dependOn(argA, argB);
        }

        @Override
        public String toString() {
            return stringFunction.apply(argA.toString(), argB.toString());
        }

        @Override
        public NodeFuncBase getFunction() {
            return NodeFuncDoubleDoubleToDouble.this;
        }

        @Override
        public int hashCode() {
            return Objects.hash(argA, argB);
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == this) return true;
            if (obj == null || getClass() != obj.getClass()) {
                return false;
            }
            FuncDoubleDoubleToDouble other = (FuncDoubleDoubleToDouble) obj;
            return Objects.equals(argA, other.argA) //
            &&Objects.equals(argB, other.argB);
        }
    }

    @FunctionalInterface
    public interface IFuncDoubleDoubleToDouble {
        double apply(double a, double b);
    }
}
