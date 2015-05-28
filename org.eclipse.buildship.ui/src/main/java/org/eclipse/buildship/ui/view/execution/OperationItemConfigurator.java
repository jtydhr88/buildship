/*
 * Copyright (c) 2015 the original author or authors.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Simon Scholz (vogella GmbH) - initial API and implementation and initial documentation
 */

package org.eclipse.buildship.ui.view.execution;

import org.eclipse.buildship.ui.PluginImage;
import org.eclipse.buildship.ui.PluginImages;
import org.eclipse.osgi.util.NLS;
import org.gradle.tooling.events.*;
import org.gradle.tooling.events.task.TaskOperationDescriptor;
import org.gradle.tooling.events.test.TestOperationDescriptor;

import java.text.DecimalFormat;

/**
 * Configures an {@code OperationItem} instance.
 */
public final class OperationItemConfigurator {

    public void configure(OperationItem operationItem) {
        FinishEvent finishEvent = operationItem.getFinishEvent();
        if (finishEvent == null) {
            operationItem.setName(getOperationSpecificName(operationItem.getStartEvent()));
            operationItem.setDuration(ExecutionsViewMessages.Tree_Item_Operation_Started_Text);
        } else {
            OperationResult result = finishEvent.getResult();
            DecimalFormat durationFormat = new DecimalFormat("#0.000"); //$NON-NLS-1$
            String duration = durationFormat.format((result.getEndTime() - result.getStartTime()) / 1000.0);
            operationItem.setDuration(NLS.bind(ExecutionsViewMessages.Tree_Item_Operation_Finished_In_0_Sec_Text, duration));
            if (result instanceof FailureResult) {
                operationItem.setImage(PluginImages.OPERATION_FAILURE.withState(PluginImage.ImageState.ENABLED).getImageDescriptor());
            } else if (result instanceof SkippedResult) {
                operationItem.setImage(PluginImages.OPERATION_SKIPPED.withState(PluginImage.ImageState.ENABLED).getImageDescriptor());
            } else if (result instanceof SuccessResult) {
                operationItem.setImage(PluginImages.OPERATION_SUCCESS.withState(PluginImage.ImageState.ENABLED).getImageDescriptor());
            }
        }
    }

    private String getOperationSpecificName(ProgressEvent event) {
        OperationDescriptor descriptor = event.getDescriptor();
        if (descriptor instanceof TaskOperationDescriptor) {
            return ((TaskOperationDescriptor) descriptor).getTaskPath();
        } else if (descriptor instanceof TestOperationDescriptor) {
            return descriptor.getName();
        } else {
            return descriptor.getDisplayName();
        }
    }

}