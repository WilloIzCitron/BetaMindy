package betamindy.content;

import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.util.*;
import betamindy.graphics.Pal2;
import mindustry.content.*;
import mindustry.ctype.ContentList;
import mindustry.entities.units.WeaponMount;
import mindustry.gen.Unit;
import mindustry.graphics.*;
import mindustry.type.StatusEffect;
import mindustry.ui.*;

public class MindyStatusEffects implements ContentList {
    public static StatusEffect radiation, controlSwap, booster, creativeShock, amnesia, ouch, icy, pause, dissonance, ideology, glitched;

    public void load(){
        radiation = new StatusEffect("radiated"){
            //credits to EyeofDarkness
            @Override
            public void update(Unit unit, float time){
                super.update(unit, time);
                if(Mathf.chanceDelta(0.008f * Mathf.clamp(time / 120f))) unit.damage(unit.maxHealth * 0.125f);
                for(int i = 0; i < unit.mounts.length; i++){
                    float strength = Mathf.clamp(time / 120f);
                    WeaponMount temp = unit.mounts[i];
                    if(temp == null) continue;
                    if(Mathf.chanceDelta(0.12f)) temp.reload = Math.min(temp.reload + Time.delta * 1.5f * strength, temp.weapon.reload);
                    temp.rotation += Mathf.range(12f * strength);
                }
            }

            {
                damage = 1.2f;
                effect = Fx.burning;
                color = Pal.heal; //very ironic, isn't it?
            }
        };

        controlSwap = new StatusEffect("controlswap"){{
            color = Pal.sapBullet;
            effect = MindyFx.exoticDust;
            effectChance = 0.4f;
            speedMultiplier = -1f;
        }};

        booster = new StatusEffect("booster"){
            @Override
            public void update(Unit unit, float time){
                if(Mathf.chanceDelta(effectChance)){
                    Tmp.v1.rnd(unit.type.hitSize /2f);
                    effect.at(unit.x + Tmp.v1.x, unit.y + Tmp.v1.y, unit.rotation);
                }
            }

            {
                color = Pal2.boostColor;
                effect = MindyFx.boostFire;
                speedMultiplier = 2.75f;
                dragMultiplier = 0.001f;
            }
        };

        icy = new StatusEffect("icy"){{
            color = Color.valueOf("b1f6fa");
            effect = MindyFx.snowflake;
            speedMultiplier = 0.3f;
            dragMultiplier = 0.5f;
            reloadMultiplier = 0.5f;
            healthMultiplier = 0.75f;
            transitionDamage = 36f;

            init(() -> {
                opposite(StatusEffects.melting, StatusEffects.burning);

                affinity(StatusEffects.blasted, ((unit, time, newTime, result) -> {
                    unit.damagePierce(transitionDamage);
                    result.set(icy, time);
                }));

                affinity(StatusEffects.wet, ((unit, time, newTime, result) -> {
                    unit.damagePierce(transitionDamage);
                    result.set(icy, time);
                }));

                affinity(StatusEffects.freezing, ((unit, time, newTime, result) -> {
                    result.set(icy, time + newTime);
                }));
            });
        }};

        amnesia = new StatusEffect("amnesia"){
            @Override
            public void update(Unit unit, float time){
                if(Mathf.chanceDelta(effectChance)){
                    Tmp.v1.rnd(unit.type.hitSize /2f);
                    effect.at(unit.x + Tmp.v1.x, unit.y + Tmp.v1.y, 0f, unit.team.color);
                }
                if(unit.abilities.size > 0) unit.abilities.clear();
            }

            {
                color = Color.valueOf("fff9d8");
                effect = MindyFx.question;
                buildSpeedMultiplier = 0.2f;
                permanent = true;
            }
        };

        creativeShock = new StatusEffect("creative-shock"){
            @Override
            public void update(Unit unit, float time){
                super.update(unit, time);
                if(unit.isBuilding()){
                    unit.clearBuilding();
                    MindyFx.forbidden.at(unit.x, unit.y, 0f, color);
                }
            }

            {
                color = Color.valueOf("ec83af");
                effect = Fx.none;
                buildSpeedMultiplier = 0f;
            }
        };

        pause = new StatusEffect("paused"){
            @Override
            public void draw(Unit unit){
                Draw.z(Layer.flyingUnit + 0.05f);
                Draw.blend(Blending.additive);
                Draw.color(Pal2.vector, Mathf.absin(Time.globalTime, 8f, 0.5f) + 0.4f);
                Draw.rect(unit.icon(), unit.x, unit.y, unit.rotation - 90f);
                Draw.blend();
                Draw.color();
            }

            {
                color = Pal2.vector;
                speedMultiplier = 0.001f;
                buildSpeedMultiplier = 0f;
                reloadMultiplier = 0f;
            }
        };

        dissonance = new StatusEffect("dissonance"){{
            damage = 4f;
            color = Color.white;
        }};

        ideology = new StatusEffect("ideology"){{
            color = Color.coral;
            effect = MindyFx.ideologied;
        }};

        //graphic status effect
        glitched = new StatusEffect("glitched"){
            @Override
            public void draw(Unit unit){
                Draw.z(Layer.flyingUnit + 0.05f);
                Draw.blend(Blending.additive);
                float f = Mathf.sin(Time.time / 5f) * 3f;
                float a = Mathf.random();
                int c = (int)(Mathf.randomSeed(unit.id) * 3f);
                Draw.color(c == 0 ? Color.magenta : c == 1 ? Color.yellow : Color.cyan, a);
                Draw.rect(unit.icon(), unit.x + f, unit.y, unit.rotation - 90f);
                Draw.color(c == 0 ? Color.cyan : c == 1 ? Color.magenta : Color.yellow, a);
                Draw.rect(unit.icon(), unit.x - f, unit.y, unit.rotation - 90f);
                Draw.blend();
                Draw.color();
            }

            {
                color = Pal2.source;
            }
        };

        //harmful blocks will ignore units with this on, as it has been damaged recently.
        ouch = new StatusEffect("ouch"){{
            color = Color.clear;
            effect = MindyFx.ideologied;
        }};
    }
}
