package example_mod.components

import com.hypixel.hytale.component.Component
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore
import io.github.dawidprosba.aergiadevkit.api.registries.annotations.HytaleComponent
import io.github.dawidprosba.aergiadevkit.ksp.hytalecodec.annotations.CodecProperty
import io.github.dawidprosba.aergiadevkit.ksp.hytalecodec.annotations.GenerateCodec

@GenerateCodec
@HytaleComponent("ComponentWithExplicitCodecBuilder")
class ComponentWithExplicitCodecBuilder  : Component<EntityStore> {
    @CodecProperty("An example property for the component")
    var testCodecProperty : String = ""
    override fun clone(): Component<EntityStore?>? {
        TODO("Not yet implemented")
    }
}