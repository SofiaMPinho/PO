TESTS=../proj-ist-unit-tests/po/2021-2022/po-p1

PO_UILIB_PATH=./po-uilib
GGC_CORE_PATH=./ggc-core
GGC_APP_PATH=./ggc-app
CLASSPATH=$(shell pwd)/po-uilib/po-uilib.jar:$(shell pwd)/ggc-app/ggc-app.jar:$(shell pwd)/ggc-core/ggc-core.jar

all:
	$(MAKE) $(MFLAGS) -C $(PO_UILIB_PATH)
	$(MAKE) $(MFLAGS) -C $(GGC_CORE_PATH)
	$(MAKE) $(MFLAGS) -C $(GGC_APP_PATH)

clean:
	$(MAKE) $(MFLAGS) clean -C $(PO_UILIB_PATH)
	$(MAKE) $(MFLAGS) clean -C $(GGC_CORE_PATH)
	$(MAKE) $(MFLAGS) clean -C $(GGC_APP_PATH)

run:
	@CLASSPATH=$(CLASSPATH) java ggc.app.App

test:
	$(MAKE) $(MFLAGS) -C $(TESTS) PROJECT_CLASSPATH=$(CLASSPATH)