package com.chsteam.shotwithmod.api

/**
 * 对于后坐力的修改只能修改Mod里的对应物品
 */
enum class BaseStats(val type: Any) {
    Horizontal_Random_Migration(type = Float),
    Vertical_Random_Migration(type = Float),
    DAMAGE(type = Double),
    SPEED(type = Double),
    MAX_AMMO(type = Int),
    SOUND_DISTANCE(type = Double),
    COOLDOWN(type = Long),
    ONCE_RELOAD(type = Int),
    RELOAD_TIME(type = Long)
}
