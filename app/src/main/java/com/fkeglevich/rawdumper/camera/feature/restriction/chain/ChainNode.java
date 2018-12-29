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

import com.fkeglevich.rawdumper.camera.feature.VirtualFeature;
import com.fkeglevich.rawdumper.camera.feature.WritableFeature;
import com.fkeglevich.rawdumper.camera.feature.restriction.chain.fixer.FeatureValueFixer;
import com.fkeglevich.rawdumper.camera.feature.restriction.chain.validator.ValidatorFactory;
import com.fkeglevich.rawdumper.camera.parameter.value.ValueValidator;

/**
 * TODO: Add class header
 * <p>
 * Created by Flávio Keglevich on 31/10/17.
 */

class ChainNode<M, S, L>
{
    private final VirtualFeature master;

    private final ValidatorFactory<M, S, L> validatorFactory;
    private final FeatureValueFixer<M, S, L> featureValueFixer;

    private final WritableFeature<S, L> slave;

    ChainNode(VirtualFeature master, ValidatorFactory<M, S, L> validatorFactory, FeatureValueFixer<M, S, L> featureValueFixer, WritableFeature<S, L> slave)
    {
        this.master = master;
        this.validatorFactory = validatorFactory;
        this.featureValueFixer = featureValueFixer;
        this.slave = slave;
    }

    void propagateChange(M newMasterValue)
    {
        getMaster().performUpdate();
        S slaveValue = slave.getValue();
        if (slave.isMutable())
        {
            ValueValidator<S, L> newSlaveValidator = validatorFactory.create(newMasterValue);

            if (!newSlaveValidator.isValid(slaveValue))
                slaveValue = featureValueFixer.fixValue(newSlaveValidator, slaveValue, newMasterValue);

            slave.changeValidator(newSlaveValidator, slaveValue);
        }
        else
        {
            slave.setValue(featureValueFixer.fixValue(slave.getValidator(), slaveValue, newMasterValue));
        }
    }

    public VirtualFeature getMaster()
    {
        return master;
    }
}
