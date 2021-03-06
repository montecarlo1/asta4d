/*
 * Copyright 2014 astamuse company,Ltd.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 */
package com.astamuse.asta4d.web.test.render.message;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.astamuse.asta4d.Context;
import com.astamuse.asta4d.web.WebApplicationConfiguration;
import com.astamuse.asta4d.web.test.WebTestBase;
import com.astamuse.asta4d.web.test.render.base.WebRenderCase;
import com.astamuse.asta4d.web.util.message.DefaultMessageRenderingHelper;
import com.astamuse.asta4d.web.util.message.DefaultMessageRenderingHelper.MessageRenderingSelector;
import com.astamuse.asta4d.web.util.message.MessageRenderingHelper;

public class DefaultMessageRendererOverrideTest extends WebTestBase {

    private static final String OriginalMessageRendererKey = DefaultMessageRendererOverrideTest.class + "#OriginalMessageRendererKey";

    @Override
    @BeforeMethod
    public void initContext() {
        super.initContext();
        WebApplicationConfiguration conf = WebApplicationConfiguration.getWebApplicationConfiguration();
        Context.getCurrentThreadContext().setData(OriginalMessageRendererKey, conf.getMessageRenderingHelper());
        conf.setMessageRenderingHelper(new DefaultMessageRenderingHelper() {
            {
                setMessageGlobalInfoSelector(new MessageRenderingSelector("#info-msg li", "span"));
                setMessageGlobalWarnSelector(new MessageRenderingSelector("#warn-msg li", "span"));
                setMessageGlobalErrSelector(new MessageRenderingSelector("#err-msg li", "span"));
            }

        });
    }

    @Override
    @AfterMethod
    public void clearContext() {
        WebApplicationConfiguration conf = WebApplicationConfiguration.getWebApplicationConfiguration();
        conf.setMessageRenderingHelper((MessageRenderingHelper) Context.getCurrentThreadContext().getData(OriginalMessageRendererKey));
        super.clearContext();
    }

    @Test()
    public void duplicatorOverride() throws Throwable {
        DefaultMessageRenderingHelper msgHelper = DefaultMessageRenderingHelper.getConfiguredInstance();

        msgHelper.info("iinnffoo1"); // in global
        msgHelper.info("iinnffoo2");// in global

        // in global
        msgHelper.outputMessage(new MessageRenderingSelector("#msg-warn-ne", "span"), msgHelper.getMessageGlobalWarnSelector(), "warn-1");

        // in place
        msgHelper.outputMessage(new MessageRenderingSelector("#msg-warn", "span"), msgHelper.getMessageGlobalWarnSelector(), "warn-2");

        // in place
        msgHelper.outputMessage(new MessageRenderingSelector("#msg-err", "span"), msgHelper.getMessageGlobalWarnSelector(), "err-1");

        // in place
        msgHelper.outputMessage(new MessageRenderingSelector("#msg-err", "span"), msgHelper.getMessageGlobalWarnSelector(), "err-2");

        new WebRenderCase("DefaultMessageRender_duplicatorOverride.html");
    }
}
