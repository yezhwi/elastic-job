/**
 * Copyright 1999-2015 dangdang.com.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * </p>
 */

package com.dangdang.example.elasticjob.spring.job;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Component;

import com.dangdang.ddframe.job.api.AbstractPerpetualElasticJob;
import com.dangdang.ddframe.job.api.JobExecutionMultipleShardingContext;
import com.dangdang.example.elasticjob.fixture.entity.Foo;
import com.dangdang.example.elasticjob.fixture.repository.FooRepository;

@Component
public class PerpetualElasticDemoJob extends AbstractPerpetualElasticJob<Foo> {
    
    private static final String LOAD_MESSAGE = "------ PerpetualElasticDemoJob load sharding items: %s at %s ------";
    
    private static final String PROCESS_MESSAGE = "------ PerpetualElasticDemoJob process data: %s at %s ------";
    
    private static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
    
    @Resource
    private FooRepository fooRepository;
    
    @Override
    protected List<Foo> fetchData(final JobExecutionMultipleShardingContext context) {
        System.out.println(String.format(LOAD_MESSAGE, context.getShardingItems(), new SimpleDateFormat(DATE_FORMAT).format(new Date())));
        return fooRepository.findActive(context.getShardingItems());
    }
    
    @Override
    protected boolean processData(final JobExecutionMultipleShardingContext context, final Foo data) {
        System.out.println(String.format(PROCESS_MESSAGE, data, new SimpleDateFormat(DATE_FORMAT).format(new Date())));
        if (9 == data.getId() % 10) {
            return false;
        }
        fooRepository.setInactive(data.getId());
        return true;
    }
}
