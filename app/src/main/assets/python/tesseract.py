import cv2
import pytesseract
import re
import PIL.Image

def flag_harmful_ingredients(text, harmful_ingredients):
    """Check the extracted text for harmful ingredients."""
    found_ingredients = []
    for ingredient in harmful_ingredients:
        if re.search(rf'\b{ingredient}\b', text, re.IGNORECASE):
            found_ingredients.append(ingredient)
    return found_ingredients

def main(image_path):
    """Main function to process the image and flag harmful ingredients."""
    harmful_ingredients = [
        "aspartame", "benzoate", "butylated hydroxyanisole", "butylated hydroxytoluene",
        "caramel coloring", "msg", "nitrate", "nitrite", "polysorbate 80", "propylene glycol",
        "red 40", "yellow 5", "yellow 6", "sodium benzoate", "sucralose"
    ]
    
    text = perform_ocr(image_path)
    flagged = flag_harmful_ingredients(text, harmful_ingredients)
    
    print("Extracted Text:")
    print(text)
    
    if flagged:
        print("\nPotential Carcinogens/Neurotoxins Detected:")
        print(", ".join(flagged))
    else:
        print("\nNo harmful ingredients detected.")


def perform_ocr(image_path):

    myConfig = r"--psm 11 --oem 3"

    extracted_text = pytesseract.image_to_string(PIL.Image.open(image_path), config=myConfig)

    print("Extracted Text:")
    print(extracted_text)
    
    return extracted_text

# Example usage
if __name__ == "__main__":
    #image_path = "C:/dev/scienceolympiad/Cheerios.jpg"  # Replace with your image path
    image_path = "C:/dev/scienceolympiad/snowballsingredient.jpg"  # Replace with actual image path
    main(image_path)
