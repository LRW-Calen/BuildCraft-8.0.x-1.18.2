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
import buildcraft.lib.expression.api.INodeFunc.INodeFuncLong;
import buildcraft.lib.expression.api.INodeStack;
import buildcraft.lib.expression.api.InvalidExpressionException;
import buildcraft.lib.expression.api.NodeTypes;
import buildcraft.lib.expression.node.func.StringFunctionQuad;
import buildcraft.lib.expression.node.func.NodeFuncBase;
import buildcraft.lib.expression.node.func.NodeFuncBase.IFunctionNode;
import buildcraft.lib.expression.node.value.NodeConstantLong;

// AUTO_GENERATED FILE, DO NOT EDIT MANUALLY!
public class NodeFuncObjectBooleanBooleanToLong<A> extends NodeFuncBase implements INodeFuncLong {

    public final IFuncObjectBooleanBooleanToLong<A> function;
    private final StringFunctionQuad stringFunction;
    private final Class<A> argTypeA;

    public NodeFuncObjectBooleanBooleanToLong(String name, Class<A> argTypeA, IFuncObjectBooleanBooleanToLong<A> function) {
        this(argTypeA, function, (a, b, c) -> "[ " + NodeTypes.getName(argTypeA) + ", boolean, boolean -> long ] " + name + "(" + a + ", " + b + ", " + c +  ")");
    }

    public NodeFuncObjectBooleanBooleanToLong(Class<A> argTypeA, IFuncObjectBooleanBooleanToLong<A> function, StringFunctionQuad stringFunction) {
        this.argTypeA = argTypeA;

        this.function = function;
        this.stringFunction = stringFunction;
    }

    @Override
    public String toString() {
        return stringFunction.apply("{A}", "{B}", "{C}");
    }

    @Override
    public NodeFuncObjectBooleanBooleanToLong<A> setNeverInline() {
        super.setNeverInline();
        return this;
    }

    @Override
    public INodeLong getNode(INodeStack stack) throws InvalidExpressionException {

        INodeBoolean c = stack.popBoolean();
        INodeBoolean b = stack.popBoolean();
        INodeObject<A> a = stack.popObject(argTypeA);

        return create(a, b, c);
    }

    /** Shortcut to create a new {@link FuncObjectBooleanBooleanToLong} without needing to create
     *  and populate an {@link INodeStack} to pass to {@link #getNode(INodeStack)}. */
    public FuncObjectBooleanBooleanToLong create(INodeObject<A> argA, INodeBoolean argB, INodeBoolean argC) {
        return new FuncObjectBooleanBooleanToLong(argA, argB, argC); 
    }

    public class FuncObjectBooleanBooleanToLong implements INodeLong, IDependantNode, IFunctionNode {
        public final INodeObject<A> argA;
        public final INodeBoolean argB;
        public final INodeBoolean argC;

        public FuncObjectBooleanBooleanToLong(INodeObject<A> argA, INodeBoolean argB, INodeBoolean argC) {
            this.argA = argA;
            this.argB = argB;
            this.argC = argC;

        }

        @Override
        public long evaluate() {
            return function.apply(argA.evaluate(), argB.evaluate(), argC.evaluate());
        }

        @Override
        public INodeLong inline() {
            if (!canInline) {
                // Note that we can still inline the arguments, just not *this* function
                return NodeInliningHelper.tryInline(this, argA, argB, argC,
                    (a, b, c) -> new FuncObjectBooleanBooleanToLong(a, b, c),
                    (a, b, c) -> new FuncObjectBooleanBooleanToLong(a, b, c)
                );
            }
            return NodeInliningHelper.tryInline(this, argA, argB, argC,
                (a, b, c) -> new FuncObjectBooleanBooleanToLong(a, b, c),
                (a, b, c) -> NodeConstantLong.of(function.apply(a.evaluate(), b.evaluate(), c.evaluate()))
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
            visitor.dependOn(argA, argB, argC);
        }

        @Override
        public String toString() {
            return stringFunction.apply(argA.toString(), argB.toString(), argC.toString());
        }

        @Override
        public NodeFuncBase getFunction() {
            return NodeFuncObjectBooleanBooleanToLong.this;
        }

        @Override
        public int hashCode() {
            return Objects.hash(argA, argB, argC);
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == this) return true;
            if (obj == null || getClass() != obj.getClass()) {
                return false;
            }
            FuncObjectBooleanBooleanToLong other = (FuncObjectBooleanBooleanToLong) obj;
            return Objects.equals(argA, other.argA) //
            &&Objects.equals(argB, other.argB) //
            &&Objects.equals(argC, other.argC);
        }
    }

    @FunctionalInterface
    public interface IFuncObjectBooleanBooleanToLong<A> {
        long apply(A a, boolean b, boolean c);
    }
}