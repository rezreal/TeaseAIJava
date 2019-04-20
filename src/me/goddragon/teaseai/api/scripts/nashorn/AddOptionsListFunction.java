package me.goddragon.teaseai.api.scripts.nashorn;

import me.goddragon.teaseai.TeaseAI;
import me.goddragon.teaseai.api.config.PersonalitiesSettingsHandler;
import me.goddragon.teaseai.api.config.PersonalitySettingsPanel;
import me.goddragon.teaseai.api.config.PersonalityVariable;
import me.goddragon.teaseai.api.scripts.personality.Personality;
import me.goddragon.teaseai.api.scripts.personality.PersonalityManager;
import me.goddragon.teaseai.utils.TeaseLogger;

import java.util.ArrayList;
import java.util.logging.Level;

public class AddOptionsListFunction extends CustomFunction
{
    public AddOptionsListFunction() {
        super("addOptionsList");
    }

    @Override
    public boolean isFunction() {
        return true;
    }

    @Override
    public Object call(Object object, Object... args) {
        super.call(object, args);

        if (args.length < 3)
        {
            TeaseLogger.getLogger().log(Level.SEVERE, "Called " + getFunctionName() + " method without parameters.");
        }
        else
        {
            Personality personality;
            if (TeaseAI.application.getSession() == null)
            {
                personality = PersonalityManager.getManager().getLoadingPersonality();
            }
            else
            {
                personality = PersonalityManager.getManager().getActivePersonality();
            }
            PersonalitySettingsPanel panel = personality.getSettingsHandler().getPanel((String)args[0]);
            if (panel == null)
            {
                TeaseLogger.getLogger().log(Level.SEVERE, "Called " + getFunctionName() + " method with invalid panel name " + args[0]);
                return null;
            }
            else
            {
                PersonalityVariable variable = personality.getVariableHandler().getVariable((String)args[1]);
                if (variable == null)
                {
                    TeaseLogger.getLogger().log(Level.SEVERE, "Called " + getFunctionName() + " method with invalid variable name " + args[1]);
                    return null;
                }
                else
                {
                    if (!PersonalitiesSettingsHandler.getHandler().hasComponent(variable))
                    {
                        PersonalitiesSettingsHandler.getHandler().addGuiComponent(variable);
                        ArrayList<String> options = new ArrayList<String>();
                        for (int i = 2; i < args.length; i++)
                        {
                            options.add((String)args[i]);
                        }
                        panel.addOptionsList(variable, options);
                    }
                }
            }
        }

        return null;
    }
}
