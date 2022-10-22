package com.example;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ChatComponentText;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.io.*;
import java.net.URL;
import java.util.concurrent.CompletableFuture;


// https://gist.githubusercontent.com/PizzaboiBestLegit/c65896b963454b679eb68a29435ccb19/raw
// Dungeon Finder > Proplayerist joined the dungeon group! (Tank Level 31)

@Mod(modid = "anticheater", version = "1.0.0")
public class ExampleMod {
    public final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    public JsonObject data = null;
    boolean shouldError = true;

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        fetchCheaters();
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onChat(ClientChatReceivedEvent event) {
        if (data == null) {
            if (shouldError && Minecraft.getMinecraft().currentScreen == null) {
                Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText("§cError fetching cheaters!"));
                shouldError = false;
                MinecraftForge.EVENT_BUS.unregister(this);
            }
            return;
        };
        String message = event.message.getUnformattedText();
        if (message.contains("Dungeon Finder > ")) {
            String ign = message.substring("Dungeon Finder > ".length()).split(" ")[0];
            if (data.has(ign)) {
                Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText("!!!!"));
                Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText("§c" + ign + " is a cheater!"));
                Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText("!!!!"));
            }
        }
    }

    public void fetchCheaters() {
        CompletableFuture.supplyAsync(() -> {
            try (Reader inReader = new InputStreamReader(new URL("https://gist.githubusercontent.com/PizzaboiBestLegit/c65896b963454b679eb68a29435ccb19/raw").openStream())) {
                data = gson.fromJson(inReader, JsonObject.class);
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
            return true;
        });
    }
}
