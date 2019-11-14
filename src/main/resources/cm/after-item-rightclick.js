function initializeCoreMod() {
	return {
		'Minecraft class rightclick transformer': {
			'target': {
				'type': 'CLASS',
				'name': "net.minecraft.client.Minecraft"
			},
			'transformer': function(classNode) {

                var ASMAPI = Java.type('net.minecraftforge.coremod.api.ASMAPI');
                var Opcodes = Java.type('org.objectweb.asm.Opcodes');
                var InsnList = Java.type('org.objectweb.asm.tree.InsnList');
                var LabelNode = Java.type('org.objectweb.asm.tree.LabelNode');
                var MethodNode = Java.type('org.objectweb.asm.tree.MethodNode');
                var InsnNode = Java.type('org.objectweb.asm.tree.InsnNode');
                var MethodInsnNode = Java.type('org.objectweb.asm.tree.MethodInsnNode');
                var FieldInsnNode = Java.type('org.objectweb.asm.tree.FieldInsnNode');
                var JumpInsnNode = Java.type('org.objectweb.asm.tree.JumpInsnNode');
                var VarInsnNode = Java.type('org.objectweb.asm.tree.VarInsnNode');

                // Step 1: Find method
                var rightClickMouse = ASMAPI.mapMethod("func_147121_ag");

                var method = null;
                var m;
                for(m = 0; m < classNode.methods.size(); m++)
                {
                    var mn = classNode.methods.get(m);
                    if(mn.name.equals(rightClickMouse))
                    {
                        method = mn;
                        break;
                    }
                }

                if (method == null)
                {
                    //return classNode;
                    throw new Error("Could not find method?!");
                }

                // Step 2: Find instruction
                var doRightClickOnBlock = ASMAPI.mapMethod("func_217292_a");

                var invokeVirtualFoundAt = -1;
                var i;
                for(i=0; i < method.instructions.size(); i++)
                {
                    var insn = method.instructions.get(i);
                    if (insn instanceof MethodInsnNode &&
                        insn.getOpcode() == Opcodes.INVOKEVIRTUAL &&
                        insn.owner.equals("net/minecraft/client/multiplayer/PlayerController") &&
                        insn.name.equals(doRightClickOnBlock))
                    {
                        invokeVirtualFoundAt = i;
                        break;
                    }
                }

                if (invokeVirtualFoundAt < 0)
                {
                    //return classNode;
                    throw new Error("Could not find method call?!");
                }

                // Will need these later :P
                var labelEndIf = new LabelNode();
                var labelElse = new LabelNode();

                // Step 3: Insert before-hook
                // Insert 2 positions after the InvokeVirtual, to account for the ASTORE
                var insertPoint1 = invokeVirtualFoundAt+2;
                var insertBefore1 = method.instructions.get(insertPoint1);
                var insertPoint0 = -1;
                var insertBefore0 = null;
                for(i=invokeVirtualFoundAt; i >= 0; i--)
                {
                    var insn = method.instructions.get(i);
                    if (insn instanceof LabelNode)
                    {
                        insertPoint0 = i;
                        insertBefore0 = insn;
                        break;
                    }
                }

                if (insertPoint0 < 0)
                {
                    //return classNode;
                    throw new Error("Could not find injection point 0?!");
                }

                list = new InsnList();
                list.add(new LabelNode()); // hook
                list.add(new VarInsnNode(Opcodes.ALOAD, 4 /*hand*/));
                list.add(new MethodInsnNode(
                        Opcodes.INVOKESTATIC, "gigaherz/stickyhands/StickyHands$Events",
                        "beforeRightClickBlock", "(Lnet/minecraft/util/Hand;)Z", false
                ));
                list.add(new JumpInsnNode(Opcodes.IFEQ, labelElse));

                method.instructions.insertBefore(insertBefore0, list);

                // Step 3: Insert after-hook
                var list = new InsnList();
                list.add(new LabelNode()); // hook
                list.add(new VarInsnNode(Opcodes.ALOAD, 4 /*hand*/));
                list.add(new VarInsnNode(Opcodes.ALOAD, 10 /*actionresulttype*/));
                list.add(new MethodInsnNode(
                        Opcodes.INVOKESTATIC, "gigaherz/stickyhands/StickyHands$Events",
                        "afterRightClickBlock", "(Lnet/minecraft/util/Hand;Lnet/minecraft/util/ActionResultType;)V", false
                ));
                list.add(new JumpInsnNode(Opcodes.GOTO, labelEndIf));
                list.add(labelElse); // hook
                list.add(new FieldInsnNode(Opcodes.GETSTATIC, "net/minecraft/util/ActionResultType", "PASS", "Lnet/minecraft/util/ActionResultType;"));
                list.add(new VarInsnNode(Opcodes.ASTORE, 10 /*actionresulttype*/));
                list.add(labelEndIf); // hook

                method.instructions.insertBefore(insertBefore1, list);

                return classNode;
			}
		}
	}
}
