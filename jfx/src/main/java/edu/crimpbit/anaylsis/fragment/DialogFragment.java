/*
 * **********************************************************************
 *
 *  Copyright (C) 2010 - 2014
 *
 *  [Component.java]
 *  JACPFX Project (https://github.com/JacpFX/JacpFX/)
 *  All rights reserved.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an "AS IS"
 *  BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 *  express or implied. See the License for the specific language
 *  governing permissions and limitations under the License.
 *
 *
 * *********************************************************************
 */

package edu.crimpbit.anaylsis.fragment;

import edu.crimpbit.anaylsis.config.ApplicationConfiguration;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import org.jacpfx.api.annotations.Resource;
import org.jacpfx.api.annotations.fragment.Fragment;
import org.jacpfx.api.fragment.Scope;
import org.jacpfx.rcp.context.Context;

/**
 * Created by Andy Moncsek on 26.06.14.
 *
 * @author Andy Moncsek
 */
@Fragment(id = ApplicationConfiguration.DIALOG_FRAGMENT,
        viewLocation = "/fxml/DialogFragment.fxml",
        resourceBundleLocation = "bundles.languageBundle",
        localeID = "en_US",
        scope = Scope.PROTOTYPE)
public class DialogFragment {

    @Resource
    private Context context;

    @FXML
    private TextField name;


    public void init() {
        name.setOnKeyReleased(event -> {
            final String nameValue = name.getText();
            if (context.getParentId().equals(ApplicationConfiguration.RECORDING_PERSPECTIVE)) {
                context.send(ApplicationConfiguration.RECORDING_PERSPECTIVE.concat(".").concat(ApplicationConfiguration.STATEFUL_CALLBACK), nameValue);
            } else {
                context.send(ApplicationConfiguration.ANALYSIS_PERSPECTIVE.concat(".").concat(ApplicationConfiguration.STATELESS_CALLBACK), nameValue);
            }
        });
    }
}
