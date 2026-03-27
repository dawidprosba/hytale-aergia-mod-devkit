VENV := .venv
PYTHON := $(VENV)/bin/python
PIP := $(VENV)/bin/pip
MKDOCS := $(VENV)/bin/mkdocs

.PHONY: install serve build clean

install:
	python -m venv $(VENV)
	$(PIP) install -r requirements.txt

serve:
	$(MKDOCS) serve

build:
	$(MKDOCS) build

clean:
	rm -rf site/
