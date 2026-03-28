VENV := .venv
PYTHON := $(VENV)/bin/python
PIP := $(VENV)/bin/pip
MKDOCS := $(VENV)/bin/mkdocs
MIKE := $(VENV)/bin/mike

.PHONY: install serve build deploy clean

install:
	python -m venv $(VENV)
	$(PIP) install -r documentation/requirements.txt

serve:
	$(MKDOCS) serve --livereload --config-file documentation/mkdocs.yml

build:
	$(MKDOCS) build --config-file documentation/mkdocs.yml

# Usage: make deploy VERSION=0.0.2
deploy:
	$(MIKE) deploy --push --update-aliases $(VERSION) latest --config-file documentation/mkdocs.yml
	$(MIKE) set-default --push latest --config-file documentation/mkdocs.yml

clean:
	rm -rf site/
