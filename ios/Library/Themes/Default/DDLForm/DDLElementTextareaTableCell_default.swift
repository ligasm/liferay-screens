/**
* Copyright (c) 2000-present Liferay, Inc. All rights reserved.
*
* This library is free software; you can redistribute it and/or modify it under
* the terms of the GNU Lesser General Public License as published by the Free
* Software Foundation; either version 2.1 of the License, or (at your option)
* any later version.
*
* This library is distributed in the hope that it will be useful, but WITHOUT
* ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
* FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
* details.
*/
import UIKit

public class DDLElementTextareaTableCell_default: DDLElementTableCell, UITextViewDelegate {

	@IBOutlet var textView: UITextView?
	@IBOutlet var placeholder: UILabel?
	@IBOutlet var textViewBackground: UIImageView?
	@IBOutlet var label: UILabel?
	@IBOutlet var separator: UIView?

	private var originalTextViewRect:CGRect = CGRectZero
	private var originalBackgroundRect:CGRect = CGRectZero

	private let expandedCellHeight:CGFloat = 104
	private let expandedTextViewHeight:CGFloat = 84
	private let expandedBackgroundHeight:CGFloat = 91

	override internal func onChangedElement() {
		if let stringElement = element as? DDLElementString {

			if stringElement.currentValue != nil {
				textView?.text = stringElement.currentStringValue
			}

			if stringElement.showLabel {
				placeholder?.text = ""
				label?.text = stringElement.label
				label?.hidden = false

				moveSubviewsVertically(0.0)
			}
			else {
				placeholder?.text = stringElement.label
				placeholder?.alpha = (textView?.text == "") ? 1.0 : 0.0
				label?.hidden = true

				moveSubviewsVertically(-(
					DDLBaseElementTextFieldTableCell_default.heightWithLabel -
					DDLBaseElementTextFieldTableCell_default.heightWithoutLabel))
				element?.currentHeight =
					DDLBaseElementTextFieldTableCell_default.heightWithoutLabel
			}

			textView?.returnKeyType = isLastCell ? .Send : .Next

			originalTextViewRect = textView!.frame
			originalBackgroundRect = textViewBackground!.frame

			if stringElement.lastValidationResult != nil {
				self.onValidated(stringElement.lastValidationResult!)
			}
		}
	}

	override public func canBecomeFirstResponder() -> Bool {
		return textView!.canBecomeFirstResponder()
	}

	override public func becomeFirstResponder() -> Bool {
		textView!.becomeFirstResponder()
		return false
	}

	override internal func onValidated(valid: Bool) {
		let imgName = valid ? "default-field" : "default-field-failed"

		textViewBackground?.image = UIImage(named: imgName)
	}

	public func textViewDidBeginEditing(textView: UITextView!) {
		var heightLabelOffset:CGFloat =
				DDLBaseElementTextFieldTableCell_default.heightWithLabel -
				DDLBaseElementTextFieldTableCell_default.heightWithoutLabel

		changeCellHeight(expandedCellHeight + (element!.showLabel ? heightLabelOffset : 0.0))

		separator!.frame.origin.y += expandedBackgroundHeight - originalBackgroundRect.size.height

		textView.frame = CGRectMake(
			self.originalTextViewRect.origin.x,
			self.originalTextViewRect.origin.y,
			self.originalTextViewRect.size.width,
			expandedTextViewHeight)
		self.textViewBackground!.frame.size.height = expandedBackgroundHeight

		textViewBackground?.highlighted = true

		formView!.firstCellResponder = textView
	}

	public func textViewDidEndEditing(textView: UITextView!) {
		separator!.frame.origin.y -= expandedBackgroundHeight - originalBackgroundRect.size.height
		textView.frame = originalTextViewRect
		textViewBackground!.frame = originalBackgroundRect

		var heightLabelOffset:CGFloat =
				DDLBaseElementTextFieldTableCell_default.heightWithLabel -
				DDLBaseElementTextFieldTableCell_default.heightWithoutLabel

		changeCellHeight(element!.editorType.registeredHeight - (element!.showLabel ? 0.0 : heightLabelOffset))

		textViewBackground?.highlighted = false
	}

	public func textView(textView: UITextView!, shouldChangeTextInRange range: NSRange, replacementText text: String!) -> Bool {

		var result = false

		if text == "\n" {
			textViewDidEndEditing(textView)
			nextCellResponder(textView)
			result = false
		} else {
			result = true

			let newText = (textView!.text as NSString).stringByReplacingCharactersInRange(range, withString:text)

			showPlaceholder(placeholder!, show:newText == "")

			element?.currentValue = newText

			if element!.lastValidationResult != nil && !element!.lastValidationResult! {
				element!.lastValidationResult = true

				onValidated(true)
			}
		}

		return result
	}

	private func showPlaceholder(placeholder:UILabel, show:Bool) {
		UIView.animateWithDuration(0.2, animations: {
			placeholder.alpha = show ? 1.0 : 0.0
		})
	}

}
