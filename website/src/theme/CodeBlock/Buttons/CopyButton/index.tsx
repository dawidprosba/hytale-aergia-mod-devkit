import React, {
  useCallback,
  useState,
  useRef,
  useEffect,
  type ReactNode,
} from 'react';
import clsx from 'clsx';
import {translate} from '@docusaurus/Translate';
import {useCodeBlockContext} from '@docusaurus/theme-common/internal';
import Button from '@theme/CodeBlock/Buttons/Button';
import type {Props} from '@theme/CodeBlock/Buttons/CopyButton';
import IconCopy from '@theme/Icon/Copy';
import IconSuccess from '@theme/Icon/Success';

import styles from './styles.module.css';

// Strip all magic comments and remove lines marked with // remove-next-line
const MAGIC_COMMENT = /^\s*\/\/ (highlight|add|remove)-(next-line|start|end)\s*$/;
const REMOVE_NEXT = /^\s*\/\/ remove-next-line\s*$/;
const REMOVE_START = /^\s*\/\/ remove-start\s*$/;
const REMOVE_END = /^\s*\/\/ remove-end\s*$/;

function getFilteredCode(codeInput: string): string {
  const lines = codeInput.split('\n');
  const result: string[] = [];
  let skipNext = false;
  let inRemoveBlock = false;

  for (const line of lines) {
    if (REMOVE_START.test(line)) { inRemoveBlock = true; continue; }
    if (REMOVE_END.test(line)) { inRemoveBlock = false; continue; }
    if (inRemoveBlock) continue;
    if (MAGIC_COMMENT.test(line)) {
      if (REMOVE_NEXT.test(line)) skipNext = true;
      continue;
    }
    if (skipNext) { skipNext = false; continue; }
    result.push(line);
  }
  return result.join('\n').replace(/\n$/, '');
}

function title() {
  return translate({
    id: 'theme.CodeBlock.copy',
    message: 'Copy',
    description: 'The copy button label on code blocks',
  });
}

function ariaLabel(isCopied: boolean) {
  return isCopied
    ? translate({
        id: 'theme.CodeBlock.copied',
        message: 'Copied',
        description: 'The copied button label on code blocks',
      })
    : translate({
        id: 'theme.CodeBlock.copyButtonAriaLabel',
        message: 'Copy code to clipboard',
        description: 'The ARIA label for copy code blocks button',
      });
}

function useCopyButton() {
  const {metadata} = useCodeBlockContext();
  const code = getFilteredCode(metadata.codeInput);
  const [isCopied, setIsCopied] = useState(false);
  const copyTimeout = useRef<number | undefined>(undefined);

  const copyCode = useCallback(() => {
    navigator.clipboard.writeText(code).then(() => {
      setIsCopied(true);
      copyTimeout.current = window.setTimeout(() => {
        setIsCopied(false);
      }, 1000);
    });
  }, [code]);

  useEffect(() => () => window.clearTimeout(copyTimeout.current), []);

  return {copyCode, isCopied};
}

export default function CopyButton({className}: Props): ReactNode {
  const {copyCode, isCopied} = useCopyButton();

  return (
    <Button
      aria-label={ariaLabel(isCopied)}
      title={title()}
      className={clsx(
        className,
        styles.copyButton,
        isCopied && styles.copyButtonCopied,
      )}
      onClick={copyCode}>
      <span className={styles.copyButtonIcons} aria-hidden="true">
        <IconCopy className={styles.copyButtonIcon} />
        <IconSuccess className={styles.copyButtonSuccessIcon} />
      </span>
    </Button>
  );
}
