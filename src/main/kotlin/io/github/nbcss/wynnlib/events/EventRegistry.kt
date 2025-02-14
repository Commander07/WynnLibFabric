package io.github.nbcss.wynnlib.events

import io.github.nbcss.wynnlib.data.CharacterProfile
import io.github.nbcss.wynnlib.function.*
import io.github.nbcss.wynnlib.readers.AbilityTreeHandler
import io.github.nbcss.wynnlib.timer.custom.HoundTimerIndicator
import io.github.nbcss.wynnlib.utils.WorldState

object EventRegistry {
    /**
     * Register all event listeners here
     */
    fun registerEvents() {
        InventoryUpdateEvent.registerListener(AbilityTreeHandler)
        ArmourStandUpdateEvent.registerListener(HoundTimerIndicator)
        RenderItemOverrideEvent.registerListener(DurabilityRender)
        InventoryRenderEvent.registerListener(CharacterInfoInventoryRender)
        SlotClickEvent.registerListener(SlotLocker.ClickListener)
        SlotPressEvent.registerListener(SlotLocker.PressListener)
        DrawSlotEvent.registerListener(SlotLocker.LockRender)
        ItemLoadEvent.registerListener(DurabilityRender.LoadListener)
        ItemLoadEvent.registerListener(AnalyzeMode)
        ItemLoadEvent.registerListener(SPNumberRender.Reader)
        RenderItemOverrideEvent.registerListener(SPNumberRender.Render)
        ItemLoadEvent.registerListener(ConsumableChargeRender.Reader)
        RenderItemOverrideEvent.registerListener(ConsumableChargeRender.Render)
        PlayerSendChatEvent.registerListener(KeyValidation)
        DrawSlotEvent.registerListener(ItemProtector.ProtectRender)
        InventoryPressEvent.registerListener(ItemProtector.PressListener)
        SlotClickEvent.registerListener(ItemProtector.ClickListener)
        SlotClickEvent.registerListener(PouchInChest.ClickListener)
        RenderItemOverrideEvent.registerListener(EmeraldPouchBarRender)
        ItemLoadEvent.registerListener(EmeraldPouchBarRender.LoadListener)
        PlayerReceiveChatEvent.registerListener(WorldState.ChatListener)
        PlayerListHeaderUpdateEvent.registerListener(WorldState.PlayerListHeaderListener)
        WorldStateChangeEvent.registerListener(CharacterProfile.WorldStateListener)
        //It does not work
        //SpellCastEvent.registerListener(ShieldIndicator.SpellTrigger)
        //ArmourStandUpdateEvent.registerListener(ShieldIndicator.EntitySpawn)
        //ClientTickEvent.registerListener(ShieldIndicator.Ticker)
    }
}