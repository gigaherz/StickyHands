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
                    return classNode;

                // Step 2: Find instruction
                var doRightClickOnBlock = ASMAPI.mapMethod("func_217292_a");

                var invokeVirtualFoundAt = -1;
                var i;
                for(i=0; i < method.instructions.size(); i++)
                {
                    var call = method.instructions.get(i);
                    if (call instanceof MethodInsnNode &&
                        call.getOpcode() == Opcodes.INVOKEVIRTUAL &&
                        call.owner.equals("net/minecraft/client/multiplayer/PlayerController") &&
                        call.name.equals(doRightClickOnBlock))
                    {
                        invokeVirtualFoundAt = i;
                        break;
                    }
                }

                if (invokeVirtualFoundAt < 0)
                    return classNode;

                // Insert 2 positions after the InvokeVirtual, to account for the ASTORE
                var insertPoint = invokeVirtualFoundAt+2;
                var insertBefore = method.instructions.get(insertPoint);

                // Step 3: Insert hook:
                var playerField = ASMAPI.mapField("field_71439_g");
                var worldField = ASMAPI.mapField("field_71441_e");

                var list = new InsnList();
                list.add(new VarInsnNode(Opcodes.ALOAD, 0 /*this*/));
                list.add(new FieldInsnNode(Opcodes.GETFIELD,"net/minecraft/client/Minecraft", playerField, "Lnet/minecraft/client/entity/player/ClientPlayerEntity;"));
                list.add(new VarInsnNode(Opcodes.ALOAD, 0 /*this*/));
                list.add(new FieldInsnNode(Opcodes.GETFIELD,"net/minecraft/client/Minecraft", worldField, "Lnet/minecraft/client/world/ClientWorld;"));
                list.add(new VarInsnNode(Opcodes.ALOAD, 4 /*hand*/));
                list.add(new VarInsnNode(Opcodes.ALOAD, 8 /*blockraytraceresult*/));
                list.add(new VarInsnNode(Opcodes.ALOAD, 10 /*actionresulttype*/));
                list.add(new MethodInsnNode(
                        Opcodes.INVOKESTATIC, "gigaherz/stickyhands/StickyHands$Events",
                        "afterRightClickBlock", "(Lnet/minecraft/client/entity/player/ClientPlayerEntity;Lnet/minecraft/client/world/ClientWorld;Lnet/minecraft/util/Hand;Lnet/minecraft/util/math/BlockRayTraceResult;Lnet/minecraft/util/ActionResultType;)V", false
                ));

                method.instructions.insertBefore(insertBefore, list);

                return classNode;
			}
		}
	}
}
