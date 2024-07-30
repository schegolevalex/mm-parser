package com.schegolevalex.mm.mmparser.parser;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.pool2.BasePooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class ParserFactory extends BasePooledObjectFactory<Parser> {
    private final ObjectFactory<Parser> objectFactory;

    @Override
    public Parser create() {
        log.info("Создан новый экземпляр Parser");
        return objectFactory.getObject();
    }

    @Override
    public PooledObject<Parser> wrap(Parser parser) {
        return new DefaultPooledObject<>(parser);
    }

    @Override
    public void destroyObject(PooledObject<Parser> p) {
        p.getObject().driver.close();
    }
}
