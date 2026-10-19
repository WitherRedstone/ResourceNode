package com.chinaex123.resource_node.init;

import com.chinaex123.resource_node.ResourceNode;
import com.chinaex123.resource_node.client.menu.ResourceNodeMenu;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public interface RNMenuTypes {
    DeferredRegister<MenuType<?>> MENU_TYPES = DeferredRegister.create(Registries.MENU, ResourceNode.MODID);

    /** 资源节点菜单类型 */
    Supplier<MenuType<ResourceNodeMenu>> RESOURCE_NODE_MENU = MENU_TYPES.register("resource_node_menu",
            () -> IMenuTypeExtension.create(ResourceNodeMenu::new));

    static void register(IEventBus eventBus) {
        MENU_TYPES.register(eventBus);
    }
}
