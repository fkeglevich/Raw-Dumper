/*
 * Copyright 2017, Flávio Keglevich
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
 */

package com.fkeglevich.rawdumper.camera.feature.restriction.chain;

import com.fkeglevich.rawdumper.camera.feature.Feature;
import com.fkeglevich.rawdumper.camera.feature.VirtualFeature;
import com.fkeglevich.rawdumper.camera.feature.WritableFeature;
import com.fkeglevich.rawdumper.camera.feature.restriction.chain.fixer.FeatureValueFixer;
import com.fkeglevich.rawdumper.camera.feature.restriction.chain.validator.ValidatorFactory;
import com.fkeglevich.rawdumper.camera.parameter.ParameterChangeEvent;
import com.fkeglevich.rawdumper.util.Assert;
import com.fkeglevich.rawdumper.util.event.EventListener;

import java.util.List;

/**
 * TODO: Add class header
 * <p>
 * Created by Flávio Keglevich on 31/10/17.
 */

public abstract class RestrictionChain
{
    private boolean onBeforeChainOccured = false;

    @SuppressWarnings("unchecked")
    protected  <M, S, L> void addChainNode(final ChainNode<M, S, L> node)
    {
        Feature<M> master = (Feature<M>) node.getMaster();
        master.getOnChanged().addListener(new EventListener<ParameterChangeEvent<M>>()
        {
            @Override
            public void onEvent(ParameterChangeEvent<M> eventData)
            {
                dispatchOnBeforeChainIfNeeded();
                node.propagateChange(eventData.parameterValue);
            }
        });
    }

    protected  <M, S, L> void setupLatestSlave(Feature<S> slave)
    {
        slave.getOnChanging().addListener(new EventListener<ParameterChangeEvent<S>>()
        {
            @Override
            public void onEvent(ParameterChangeEvent<S> eventData)
            {
                dispatchOnBeforeChainIfNeeded();
            }
        });

        slave.getOnChanged().addListener(new EventListener<ParameterChangeEvent<S>>()
        {
            @Override
            public void onEvent(ParameterChangeEvent<S> eventData)
            {
                dispatchOnAfterChainIfNeeded();
            }
        });
    }

    protected abstract void onBeforeChain();

    protected abstract void onAfterChain();

    private void dispatchOnBeforeChainIfNeeded()
    {
        if (!onBeforeChainOccured)
        {
            onBeforeChain();
            onBeforeChainOccured = true;
        }
    }

    private void dispatchOnAfterChainIfNeeded()
    {
        if (onBeforeChainOccured)
        {
            onAfterChain();
            onBeforeChainOccured = false;
        }
    }
}
