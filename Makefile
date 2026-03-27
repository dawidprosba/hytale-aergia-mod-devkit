VENV := .venv
PYTHON := $(VENV)/bin/python
PIP := $(VENV)/bin/pip
MKDOCS := $(VENV)/bin/mkdocs
MIKE := $(VENV)/bin/mike

.PHONY: install serve build deploy clean

install:
	python -m venv $(VENV)
	$(PIP) install -r requirements.txt

serve:
	$(MKDOCS) serve --livereload

build:
	$(MKDOCS) build

# Usage: make deploy VERSION=0.0.2
deploy:
	$(MIKE) deploy --push --update-aliases $(VERSION) latest
	$(MIKE) set-default --push latest

clean:
	rm -rf site/
